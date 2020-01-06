package com.abclinic.server.controller;

import com.abclinic.server.base.BaseController;
import com.abclinic.server.base.Views;
import com.abclinic.server.exception.BadRequestException;
import com.abclinic.server.exception.ForbiddenException;
import com.abclinic.server.exception.NotFoundException;
import com.abclinic.server.model.entity.ImageAlbum;
import com.abclinic.server.model.entity.Image;
import com.abclinic.server.model.entity.user.Patient;
import com.abclinic.server.model.entity.user.User;
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
import javax.swing.text.View;
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
@Api(tags = "Album và ảnh")
@RequestMapping(value = "/images")
public class ImageController extends BaseController {

    @Value("${file.upload-dir}")
    private String uploadDirectory;

    public ImageController(UserRepository userRepository, PractitionerRepository practitionerRepository, PatientRepository patientRepository, CoordinatorRepository coordinatorRepository, DietitianRepository dietitianRepository, SpecialistRepository specialistRepository, AlbumRepository albumRepository, ImageRepository imageRepository, MedicalRecordRepository medicalRecordRepository, DietitianRecordRepository dietitianRecordRepository, QuestionRepository questionRepository, ReplyRepository replyRepository, SpecialtyRepository specialtyRepository, DiseaseRepository diseaseRepository) {
        super(userRepository, practitionerRepository, patientRepository, coordinatorRepository, dietitianRepository, specialistRepository, albumRepository, imageRepository, medicalRecordRepository, dietitianRecordRepository, questionRepository, replyRepository, specialtyRepository, diseaseRepository);
    }

    @Override
    public void init() {
        logger = LoggerFactory.getLogger(ImageController.class);
    }

    @PostMapping(value = "")
    @ApiOperation(value = "Upload một hoặc nhiều ảnh", notes = "Trả về album ảnh hoặc 400 BAD REQUEST")
    @ApiImplicitParams({
//            @ApiImplicitParam(name = "files", value = "Các file ảnh được gửi lên", required = true, allowMultiple = true, dataType = "file[]"),
            @ApiImplicitParam(name = "type", value = "Loại bữa ăn (sáng, trưa, tối, thêm)", allowableValues = "0, 1, 2, 3", required = true, dataType = "int", example = "0"),
            @ApiImplicitParam(name = "content", value = "Mô tả bữa ăn", required = true, dataType = "string", example = "Thực đơn: A, B, C")
    })
    @ApiResponses({
            @ApiResponse(code = 201, message = "Upload ảnh thành công"),
            @ApiResponse(code = 400, message = "File không hợp lệ")
    })
    @ResponseStatus(HttpStatus.CREATED)
    @JsonView(Views.Public.class)
    public ResponseEntity<ImageAlbum> processUpload(@ApiIgnore @RequestAttribute("User") User user,
                                                    @RequestParam("files") MultipartFile[] files,
                                                    @RequestParam("type") int type,
                                                    @RequestParam("content") String content) {
        Patient patient = patientRepository.findById(user.getId());
        if (files.length == 0)
            throw new BadRequestException(patient.getId(), "must upload at least 1 image");
        Album album = GooglePhotosService.makeAlbum();
        ImageAlbum imageAlbum = new ImageAlbum(album.getId(), patient, content, type);
        save(imageAlbum);

        try {
            List<Image> images = new ArrayList<>();
            for (MultipartFile file: files) {
                images.add(upload(file, album, imageAlbum));
            }
            images.forEach(this::save);
            return new ResponseEntity<>(albumRepository.findById(imageAlbum.getId()).get(), HttpStatus.CREATED);
        } catch (Exception e) {
            throw new BadRequestException(patient.getId());
        }
    }

    @PostMapping(value = "/avatar")
    @ApiOperation(value = "Upload ảnh avatar", notes = "Trả về ảnh avatar hoặc 400 BAD REQUEST", consumes = "multipart/form-data")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "file", value = "File ảnh được gửi lên", required = true, dataType = "file", paramType = "formData")
//    })
    @ApiResponses({
            @ApiResponse(code = 201, message = "Upload ảnh avatar thành công"),
            @ApiResponse(code = 400, message = "File không hợp lệ")
    })
    @ResponseStatus(HttpStatus.CREATED)
    @JsonView(Views.Public.class)
    public ResponseEntity<String> processUploadAvatar(@ApiIgnore @RequestAttribute("User") User user,
                                                     @RequestParam("file") MultipartFile file) {
        Album album;
        Optional<Album> op = GooglePhotosService.getAlbumByName("Avatar");
        album = op.orElseGet(() -> GooglePhotosService.makeAlbum("Avatar"));
        try {
            Image avatar = upload(file, album, null);
            user.setAvatar(avatar.getPath());
            save(user);
            return new ResponseEntity<>(GooglePhotosService.getImage(avatar.getPath()), HttpStatus.CREATED);
        } catch (Exception e) {
            throw new BadRequestException(user.getId());
        }
    }

    private Image upload(MultipartFile file, Album album, ImageAlbum imageAlbum) throws Exception {
        String fileName = Objects.requireNonNull(file.getOriginalFilename()).substring(0, file.getOriginalFilename().lastIndexOf('.'));
        String fileType = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf('.') + 1);
        File f = toFile(fileName, fileType, file.getBytes());
        String token = GooglePhotosService.getToken(fileName, f.getPath());
        String uid = GooglePhotosService.createItem(token, album.getId(), f.getName());
        return new Image(uid, imageAlbum, fileName, fileType);
    }

    @GetMapping(value = "/albums")
    @ApiOperation(value = "Lấy tất cả album ảnh của cá nhân", notes = "Trả về danh sách album ảnh hoặc 404 NOT FOUND")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Lấy danh sách album thành công"),
            @ApiResponse(code = 400, message = "Người dùng không có ảnh nào")
    })
    @ResponseStatus(HttpStatus.OK)
    @JsonView(Views.Public.class)
    public ResponseEntity<List<ImageAlbum>> processGetAllAlbums(@ApiIgnore @NotNull @RequestHeader("Authorization") String userUid) {
        Patient patient = patientRepository.findByUid(userUid).get();
        Optional<List<ImageAlbum>> opt = albumRepository.findByPatient(patient);
        if (opt.isPresent()) {
            return new ResponseEntity<>(opt.get(), HttpStatus.OK);
        } else throw new NotFoundException(patient.getId());
    }

    @GetMapping(value = "/albums/{album-id}")
    @ApiOperation(value = "Lấy tất cả ảnh của một album", notes = "Trả về danh sách link ảnh hoặc 404 NOT FOUND")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "album-id", value = "Mã ID của album", required = true, dataType = "long", paramType = "path", example = "1")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Lấy danh sách ảnh thành công"),
            @ApiResponse(code = 400, message = "Album không có ảnh nào hoặc không tồn tại"),
            @ApiResponse(code = 404, message = "Người dùng không được xem ảnh trong album của người khác")
    })
    @ResponseStatus(HttpStatus.OK)
    @JsonView(Views.Public.class)
    public ResponseEntity<List<String>> processGetAlbum(@ApiIgnore @NotNull @RequestHeader("Authorization") String userUid,
                                                      @PathVariable("album-id") long albumId) {
        Patient patient = patientRepository.findByUid(userUid).get();
        Optional<ImageAlbum> opt = albumRepository.findById(albumId);
        if (opt.isPresent()) {
            if (opt.get().getPatient().equals(patient)) {
                List<String> images = GooglePhotosService.getAlbumImages(opt.get().getUid());
                return new ResponseEntity<>(images, HttpStatus.OK);
            }
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
