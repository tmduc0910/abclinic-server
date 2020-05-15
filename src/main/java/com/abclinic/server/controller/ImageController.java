package com.abclinic.server.controller;

import com.abclinic.server.annotation.authorized.Restricted;
import com.abclinic.server.common.base.CustomController;
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
                                                  @RequestAttribute("files") MultipartFile[] files) {
        Patient patient = patientService.getById(user.getId());
        if (files.length == 0)
            throw new BadRequestException(patient.getId(), "phải ít nhất upload lên 1 ảnh");
//        Album album = GooglePhotosService.makeAlbum();

        try {
            CloudinaryService service = CloudinaryService.getInstance(fileService.getUploadDirectory());
//            List<Image> images = new ArrayList<>();
//            for (MultipartFile file : files) {
//                images.add(upload(file, album));
//            }
//            AlbumDto albumDto = new AlbumDto(album.getId(), images.stream()
            String albumId = service.getTag(false);
            List<Image> images = service.uploadImages(files, albumId);
            AlbumDto albumDto = new AlbumDto(albumId, images.stream()
                    .map(i -> i = imageService.save(i))
                    .map(Image::getPath)
                    .collect(Collectors.toList()));
            service.getImages(albumId);

            return new ResponseEntity<>(albumDto, HttpStatus.CREATED);
        } catch (Exception e) {
            throw new BadRequestException(patient.getId());
        }
    }

    @PostMapping(value = "/avatar")
    @ApiOperation(value = "Upload ảnh avatar", notes = "Trả về ảnh avatar hoặc 400 BAD REQUEST", consumes = "multipart/form-data")
    @ApiResponses({
            @ApiResponse(code = 201, message = "Upload ảnh avatar thành công"),
            @ApiResponse(code = 400, message = "File không hợp lệ")
    })
    @JsonView(Views.Public.class)
    public ResponseEntity<String> processUploadAvatar(@ApiIgnore @RequestAttribute("User") User user,
                                                      @RequestAttribute("file") MultipartFile file) {
//        Album album;
//        Optional<Album> op = GooglePhotosService.getAlbumByName("Avatar");
//        album = op.orElseGet(() -> GooglePhotosService.makeAlbum("Avatar"));
        try {
//            Image avatar = upload(file, album);
            CloudinaryService service = CloudinaryService.getInstance(fileService.getUploadDirectory());
            Image avatar = service.uploadAvatar(file);
            user.setAvatar(avatar.getPath());
            userService.save(user);
            return new ResponseEntity<>(avatar.getPath(), HttpStatus.CREATED);
        } catch (Exception e) {
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
        CloudinaryService service = CloudinaryService.getInstance(fileService.getUploadDirectory());
        try {
            return new ResponseEntity<>(service.getImages(albumId), HttpStatus.OK);
        } catch (Exception e) {
            throw new NotFoundException();
        }
    }

//    @GetMapping(value = "/albums")
//    @ApiOperation(value = "Lấy tất cả album ảnh của cá nhân", notes = "Trả về danh sách album ảnh hoặc 404 NOT FOUND")
//    @ApiResponses({
//            @ApiResponse(code = 200, message = "Lấy danh sách album thành công"),
//            @ApiResponse(code = 400, message = "Người dùng không có ảnh nào")
//    })
//    @ResponseStatus(HttpStatus.OK)
//    @JsonView(Views.Public.class)
//    public ResponseEntity<List<Inquiry>> processGetAllAlbums(@ApiIgnore @RequestAttribute("User") User user) {
//        Patient patient = patientService.getById(user.getId());
//        return new ResponseEntity<>(inquiryService.getByPatient(patient), HttpStatus.OK);
//    }

//    @GetMapping(value = "/albums/{album-id}")
//    @ApiOperation(value = "Lấy tất cả ảnh của một album", notes = "Trả về danh sách link ảnh hoặc 404 NOT FOUND")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "album-id", value = "Mã ID của album", required = true, dataType = "long", paramType = "path", example = "1")
//    })
//    @ApiResponses({
//            @ApiResponse(code = 200, message = "Lấy danh sách ảnh thành công"),
//            @ApiResponse(code = 400, message = "Album không có ảnh nào hoặc không tồn tại"),
//            @ApiResponse(code = 404, message = "Người dùng không được xem ảnh trong album của người khác")
//    })
//    @ResponseStatus(HttpStatus.OK)
//    @JsonView(Views.Public.class)
//    public ResponseEntity<List<String>> processGetAlbum(@ApiIgnore @RequestAttribute("User") User user,
//                                                        @PathVariable("album-id") long albumId) {
//        Patient patient = patientService.getById(user.getId());
//        Inquiry inquiry = inquiryService.getById(albumId);
//        if (inquiry.getPatient().equals(patient)) {
////                List<String> images = GooglePhotosService.getAlbumImages(opt.get().getAlbum());
//            return new ResponseEntity<>(inquiry.getAlbum(), HttpStatus.OK);
//        } else throw new ForbiddenException(patient.getId());
//    }
}
