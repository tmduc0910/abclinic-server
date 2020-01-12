package com.abclinic.server.controller.deprecated;

        import com.abclinic.server.base.BaseController;
        import com.abclinic.server.base.Views;
        import com.abclinic.server.constant.Role;
        import com.abclinic.server.constant.Status;
        import com.abclinic.server.exception.NotFoundException;
        import com.abclinic.server.model.entity.user.User;
        import com.abclinic.server.repository.*;
        import com.fasterxml.jackson.annotation.JsonView;
        import io.swagger.annotations.*;
        import org.slf4j.LoggerFactory;
        import org.springframework.data.domain.PageRequest;
        import org.springframework.data.domain.Pageable;
        import org.springframework.data.domain.Sort;
        import org.springframework.http.HttpStatus;
        import org.springframework.http.ResponseEntity;
        import org.springframework.web.bind.annotation.*;
        import springfox.documentation.annotations.ApiIgnore;

        import java.util.Optional;

/**
 * @author tmduc
 * @package com.abclinic.server.controller
 * @created 12/3/2019 2:59 PM
 */
@RestController
@Api(tags = "Nhân viên phòng khám")
@RequestMapping("/admin")
public class DoctorController extends BaseController {

    @Override
    public void init() {
        this.logger = LoggerFactory.getLogger(DoctorController.class);
    }

//    @GetMapping("/doctors/dietitian")
//    public ResponseEntity<List<Dietitian>> getAllDietitian() {
//        return new ResponseEntity<>(dietitianRepository.findAll(), HttpStatus.OK);
//    }
//
//    @GetMapping("/doctors/dietitian/{id}")
//    public ResponseEntity<Dietitian> getDietitian(@RequestHeader("user-id") int userId, @PathVariable int id) {
//        return dietitianRepository.findById(id).map(dietitian -> new ResponseEntity<>(dietitian, HttpStatus.OK)).orElseThrow(() -> new BadRequestException(userId));
//    }
//
//    @GetMapping("/doctors/practitioner")
//    public ResponseEntity<List<Practitioner>> getAllPractitioner() {
//        return new ResponseEntity<>(practitionerRepository.findAll(), HttpStatus.OK);
//    }
//
//    @GetMapping("/doctors/practitioner/{id}")
//    public ResponseEntity<Practitioner> getPractitioner(@RequestHeader("user-id") int userId, @PathVariable int id) {
//        return practitionerRepository.findById(id).map(practitioner -> new ResponseEntity<>(practitioner, HttpStatus.OK)).orElseThrow(() -> new BadRequestException(userId));
//    }
//
//    @GetMapping("/doctors/specialist")
//    public ResponseEntity<List<Specialist>> getAllSpecialist() {
//        return new ResponseEntity<>(specialistRepository.findAll(), HttpStatus.OK);
//    }
//
//    @GetMapping("/doctors/specialist/{id}")
//    public ResponseEntity<Specialist> getSpecialist(@RequestHeader("user-id") int userId, @PathVariable int id) {
//        return specialistRepository.findById(id).map(specialist -> new ResponseEntity<>(specialist, HttpStatus.OK)).orElseThrow(() -> new BadRequestException(userId));
//    }
//
//    @GetMapping("/specialties")
//    public ResponseEntity<List<Specialty>> getSpecialties() {
//        return new ResponseEntity<>(specialtyRepository.findAll(), HttpStatus.OK);
//    }


}
