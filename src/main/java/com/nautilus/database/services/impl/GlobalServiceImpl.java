package com.nautilus.database.services.impl;

import com.nautilus.database.repository.CarRepository;
import com.nautilus.database.repository.UserRepository;
import com.nautilus.database.services.def.GlobalService;
import org.springframework.beans.factory.annotation.Autowired;

public class GlobalServiceImpl implements GlobalService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CarRepository carRepository;

}
