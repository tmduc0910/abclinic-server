package com.abclinic.server.controller;

import com.abclinic.server.base.BaseController;
import com.abclinic.server.base.Views;
import com.abclinic.server.exception.ForbiddenException;
import com.abclinic.server.model.entity.ImageAlbum;
import com.abclinic.server.model.entity.Image;
import com.abclinic.server.model.entity.user.Patient;
import com.abclinic.server.repository.*;
import com.abclinic.server.service.ImageService;
import com.fasterxml.jackson.annotation.JsonView;
import com.google.photos.types.proto.Album;
import org.apache.commons.io.FileUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.abclinic.server.service.ImageService.makeAlbum;

@RestController
@RequestMapping(value = "/image")
public class ImageController extends BaseController {

    @Value("${file.upload-dir}")
    private String uploadDirectory;

    @Autowired
    public ImageController(UserRepository userRepository, PractitionerRepository practitionerRepository, PatientRepository patientRepository, CoordinatorRepository coordinatorRepository, DietitianRepository dietitianRepository, SpecialistRepository specialistRepository, AlbumRepository albumRepository, ImageRepository imageRepository, MedicalRecordRepository medicalRecordRepository) {
        super(userRepository, practitionerRepository, patientRepository, coordinatorRepository, dietitianRepository, specialistRepository, albumRepository, imageRepository, medicalRecordRepository);
    }

    @PostMapping(value = "/upload")
    @JsonView(Views.Public.class)
    public ResponseEntity<List<Image>> upload(@NotNull @RequestHeader("user-id") int userId, @RequestParam("file-name") String fileName, @RequestParam("file-type") String fileType, @RequestParam("file") MultipartFile... files) {
        Optional<Patient> patient = patientRepository.findById(userId);
        if (!patient.isPresent())
            throw new ForbiddenException(userId);
        Album album = ImageService.makeAlbum();
        ImageAlbum imageAlbum = new ImageAlbum(album.getId(), patient.get(), album.getTitle());
        save(imageAlbum);

        try {
            List<Image> images = new ArrayList<>();
            imageAlbum = albumRepository.findById(imageAlbum.getId()).get();
            for (MultipartFile file: files) {
                File f = toFile(fileName, fileType, file.getBytes());
                String token = ImageService.getToken(fileName, f.getPath());
                String path = ImageService.createItem(token, album.getId(), f.getName());
                images.add(new Image(imageAlbum, fileName, fileType, path));
            }
            images.forEach(this::save);
            return new ResponseEntity<>(images, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    private File toFile(String name, String type, byte[] bytes) {
        try {
            File file = new File(uploadDirectory + name + "." + type);
            FileUtils.writeByteArrayToFile(file, bytes);
            return file;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void init() {
        logger = LoggerFactory.getLogger(ImageController.class);
    }
}
