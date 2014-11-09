package com.pacoworks.reactiveinputs;

import lombok.Getter;
import lombok.NonNull;

/**
* Created by Paco on 09/11/2014.
* See LICENSE.md
*/
public enum KEY_WRAPPER {
    UNKNOWN("?"), KEY_UP("Up"), KEY_DOWN("Down"), KEY_LEFT("Left"), KEY_RIGHT("Right"), KEY_ONE("One");
    @Getter
    private final String mKeyName;

    private KEY_WRAPPER(@NonNull String keyName) {
        mKeyName = keyName;
    }

    private static KEY_WRAPPER fromKeycode(int keyCode){
        KEY_WRAPPER[] values = values();
        if (keyCode < 1 || keyCode >= values.length){
            return UNKNOWN;
        }
        return values[keyCode];
    }
}
