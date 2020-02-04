package com.abclinic.server.controller;

import com.abclinic.server.base.BaseController;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author tmduc
 * @package com.abclinic.server.controller
 * @created 1/11/2020 2:42 PM
 */
@RestController
public class PatientResourceController extends BaseController {

    @Override
    public void init() {
        this.logger = LoggerFactory.getLogger(PatientResourceController.class);
    }


}
