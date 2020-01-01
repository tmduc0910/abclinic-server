package com.abclinic.server.controller;

import com.abclinic.server.base.BaseController;
import com.abclinic.server.exception.BadRequestException;
import com.abclinic.server.exception.ForbiddenException;
import com.abclinic.server.exception.NotFoundException;
import com.abclinic.server.model.entity.ImageAlbum;
import com.abclinic.server.model.entity.Image;
import com.abclinic.server.model.entity.user.Patient;
import com.abclinic.server.repository.*;
import com.abclinic.server.service.GooglePhotosService;
import com.fasterxml.jackson.annotation.JsonView;
import com.google.photos.types.proto.Album;
import io.swagger.annotations.*;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import javax.imageio.ImageIO;
import javax.validation.constraints.NotNull;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * @author tmduc
 * @package com.abclinic.server.controller
 * @created 11/23/2019 3:38 PM
 */
@RestController
@RequestMapping(value = "/images")
public class ImageController extends BaseController {

    @Value("${file.upload-dir}")
    private String uploadDirectory;

    public ImageController(UserRepository userRepository, PractitionerRepository practitionerRepository, PatientRepository patientRepository, CoordinatorRepository coordinatorRepository, DietitianRepository dietitianRepository, SpecialistRepository specialistRepository, AlbumRepository albumRepository, ImageRepository imageRepository, MedicalRecordRepository medicalRecordRepository, QuestionRepository questionRepository, ReplyRepository replyRepository, SpecialtyRepository specialtyRepository) {
        super(userRepository, practitionerRepository, patientRepository, coordinatorRepository, dietitianRepository, specialistRepository, albumRepository, imageRepository, medicalRecordRepository, questionRepository, replyRepository, specialtyRepository);
    }

    @Override
    public void init() {
        logger = LoggerFactory.getLogger(ImageController.class);
    }

    @PostMapping(value = "/upload")
    @ApiOperation(value = "Upload một hoặc nhiều ảnh", notes = "Trả về 201 CREATED hoặc 400 BAD REQUEST")
    @ApiImplicitParams(@ApiImplicitParam(name = "files", value = "Các file ảnh được gửi lên", required = true, allowMultiple = true, dataType = "MultipartFile[]", paramType = "body"))
    @ApiResponses({
            @ApiResponse(code = 201, message = "Upload ảnh thành công"),
            @ApiResponse(code = 400, message = "File không hợp lệ")
    })
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity processUpload(@ApiIgnore @NotNull @RequestHeader("Authorization") String userUid,
                                        @RequestParam("files") MultipartFile[] files) {
        Optional<Patient> patient = patientRepository.findByUid(userUid);
        if (files.length == 0)
            throw new BadRequestException(patient.get().getId(), "must upload at least 1 image");
        Album album = GooglePhotosService.makeAlbum();
        ImageAlbum imageAlbum = new ImageAlbum(album.getId(), patient.get(), album.getTitle());
        save(imageAlbum);

        try {
            List<Image> images = new ArrayList<>();
            for (MultipartFile file: files) {
                String fileName = Objects.requireNonNull(file.getOriginalFilename()).substring(0, file.getOriginalFilename().lastIndexOf('.'));
                String fileType = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf('.') + 1);
                File f = toFile(fileName, fileType, file.getBytes());
                String token = GooglePhotosService.getToken(fileName, f.getPath());
                String path = GooglePhotosService.createItem(token, album.getId(), f.getName());
                images.add(new Image(imageAlbum, fileName, fileType, path));
            }
            images.forEach(this::save);
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (Exception e) {
            throw new BadRequestException(patient.get().getId());
        }
    }

    @GetMapping(value = "/albums")
    @ApiOperation(value = "Lấy tất cả album ảnh của cá nhân", notes = "Trả về danh sách album ảnh hoặc 404 NOT FOUND")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Lấy danh sách album thành công"),
            @ApiResponse(code = 400, message = "Người dùng không có ảnh nào")
    })
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<ImageAlbum>> processGetAllAlbums(@ApiIgnore @NotNull @RequestHeader("Authorization") String userUid) {
        Patient patient = patientRepository.findByUid(userUid).get();
        Optional<List<ImageAlbum>> opt = albumRepository.findByPatient(patient);
        if (opt.isPresent()) {
            return new ResponseEntity<>(opt.get(), HttpStatus.OK);
        } else throw new NotFoundException(patient.getId());
    }

    @GetMapping(value = "/albums/{album-id}")
    @ApiOperation(value = "Lấy tất cả ảnh của một album", notes = "Trả về danh sách ảnh hoặc 404 NOT FOUND")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "albumId", value = "Mã ID của album", required = true, dataType = "long", paramType = "path")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Lấy danh sách ảnh thành công"),
            @ApiResponse(code = 400, message = "Album không có ảnh nào hoặc không tồn tại"),
            @ApiResponse(code = 404, message = "Người dùng không được xem ảnh trong album của người khác")
    })
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ImageAlbum> processGetAlbum(@ApiIgnore @NotNull @RequestHeader("Authorization") String userUid,
                                                      @PathVariable("album-id") long albumId) {
        Patient patient = patientRepository.findByUid(userUid).get();
        Optional<ImageAlbum> opt = albumRepository.findById(albumId);
        if (opt.isPresent()) {
            if (opt.get().getPatient().equals(patient))
                return new ResponseEntity<>(opt.get(), HttpStatus.OK);
            else throw new ForbiddenException(patient.getId());
        } else throw new NotFoundException(patient.getId());
    }

    private File toFile(String name, String type, byte[] bytes) {
        try {
            File file = new File(uploadDirectory + name + "." + type);
            FileUtils.writeByteArrayToFile(file, bytes);
            try (InputStream is = new FileInputStream(file)) {
                BufferedImage image = ImageIO.read(is);
                try (OutputStream os = new FileOutputStream(file)) {
                    ImageIO.write(image, type, os);
                } catch (Exception exp) {
                    exp.printStackTrace();
                }
            } catch (Exception exp) {
                exp.printStackTrace();
            }
            return file;
        } catch (IOException e) {
            logger.error(e.getLocalizedMessage());
            return null;
        }
    }
}
