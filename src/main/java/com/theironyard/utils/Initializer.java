package com.theironyard.utils;

import com.theironyard.configs.Config;
import org.springframework.session.web.context.AbstractHttpSessionApplicationInitializer;

/**
 * Created by PiratePowWow on 4/7/16.
 */
public class Initializer extends AbstractHttpSessionApplicationInitializer {

    public Initializer() {
        super(Config.class);
    }
}