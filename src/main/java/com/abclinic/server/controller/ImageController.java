package com.abclinic.server.controller;

import com.abclinic.server.annotation.authorized.Restricted;
import com.abclinic.server.common.base.CustomController;
import com.abclinic.server.common.base.CustomRuntimeException;
import com.abclinic.server.common.base.Views;
import com.abclinic.server.common.utils.FileUtils;
import com.abclinic.server.exception.BadRequestException;
import com.abclinic.server.exception.ForbiddenException;
import com.abclinic.server.exception.NotFoundException;
import com.abclinic.server.model.dto.AlbumDto;
import com.abclinic.server.model.dto.request.post.RequestCreateAvatarDto;
import com.abclinic.server.model.dto.request.post.RequestCreateImageDto;
import com.abclinic.server.model.entity.Image;
import com.abclinic.server.model.entity.payload.Inquiry;
import com.abclinic.server.model.entity.user.Coordinator;
import com.abclinic.server.model.entity.user.Patient;
import com.abclinic.server.model.entity.user.User;
import com.abclinic.server.service.CloudinaryService;
import com.abclinic.server.service.FileService;
import com.abclinic.server.service.GooglePhotosService;
import com.abclinic.server.service.entity.ImageService;
import com.abclinic.server.service.entity.InquiryService;
import com.abclinic.server.service.entity.PatientService;
import com.abclinic.server.service.entity.UserService;
import com.fasterxml.jackson.annotation.JsonView;
import com.google.photos.types.proto.Album;
import io.swagger.annotations.*;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import java.io.*;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author tmduc
 * @package com.abclinic.server.controller
 * @created 11/23/2019 3:38 PM
 */
@RestController
@Api(tags = "Album và ảnh")
@RequestMapping(value = "/images")
public class ImageController extends CustomController {
    @Autowired
    private ImageService imageService;

    @Autowired
    private UserService userService;

    @Autowired
    private InquiryService inquiryService;

    @Autowired
    private PatientService patientService;

    @Autowired
    private FileService fileService;

    @Autowired
    private CloudinaryService cloudinaryService;

    @Override
    public void init() {
        logger = LoggerFactory.getLogger(ImageController.class);
    }

    @PostMapping(value = "")
    @ApiOperation(value = "Upload một hoặc nhiều ảnh", notes = "Trả về album ảnh hoặc 400 BAD REQUEST")
    @ApiResponses({
            @ApiResponse(code = 201, message = "Upload ảnh thành công"),
            @ApiResponse(code = 400, message = "File không hợp lệ")
    })
    public ResponseEntity<AlbumDto> processUpload(@ApiIgnore @RequestAttribute("User") User user,
                                                  @RequestPart("files") MultipartFile[] files) {
        Patient patient = patientService.getById(user.getId());
        if (files.length == 0)
            throw new BadRequestException(patient.getId(), "phải ít nhất upload lên 1 ảnh");

        try {
            String albumId = cloudinaryService.getTag(false);
            List<Image> images = cloudinaryService.uploadImages(files, albumId);
            AlbumDto albumDto = new AlbumDto(albumId, images.stream()
                    .map(i -> i = imageService.save(i))
                    .map(Image::getPath)
                    .collect(Collectors.toList()));
            cloudinaryService.getImages(albumId);

            return new ResponseEntity<>(albumDto, HttpStatus.CREATED);
        } catch (Exception e) {
            throw new BadRequestException(patient.getId());
        }
    }

    @PostMapping(value = "/avatar")
    @ApiOperation(value = "Upload ảnh avatar", notes = "Trả về ảnh avatar hoặc 400 BAD REQUEST")
    @ApiResponses({
            @ApiResponse(code = 201, message = "Upload ảnh avatar thành công"),
            @ApiResponse(code = 400, message = "File không hợp lệ")
    })
    @JsonView(Views.Public.class)
    public ResponseEntity<String> processUploadAvatar(@ApiIgnore @RequestAttribute("User") User user,
                                                      @RequestPart("files") MultipartFile[] files) {
        try {
            Image avatar = cloudinaryService.uploadAvatar(files[0]);
            user.setAvatar(avatar.getPath());
            userService.save(user);
            return new ResponseEntity<>(avatar.getPath(), HttpStatus.CREATED);
        } catch (CustomRuntimeException e) {
            throw new BadRequestException(user.getId());
        } catch (IOException e) {
            e.printStackTrace();
            throw new BadRequestException(user.getId());
        }
    }

    private Image upload(MultipartFile file, Album album) throws Exception {
        String fileName = Objects.requireNonNull(file.getOriginalFilename()).substring(0, file.getOriginalFilename().lastIndexOf('.'));
        String fileType = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf('.') + 1);
        File f = FileUtils.toFile(fileName, fileType, file.getBytes(), fileService.getUploadDirectory());
        String token = GooglePhotosService.getToken(fileName, f.getPath());
        String imageId = GooglePhotosService.createItem(token, album.getId(), f.getName());
        return new Image(imageId, fileName, fileType);
    }

    @GetMapping("")
    @Restricted(excluded = Coordinator.class)
    public ResponseEntity<List<String>> getImages(@RequestParam("album_id") String albumId) {
        try {
            return new ResponseEntity<>(cloudinaryService.getImages(albumId), HttpStatus.OK);
        } catch (Exception e) {
            throw new NotFoundException();
        }
    }
}
