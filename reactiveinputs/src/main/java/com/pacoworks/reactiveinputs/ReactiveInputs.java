
package com.pacoworks.reactiveinputs;

import lombok.Data;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import rx.Observable;

/**
 * Created by Paco on 09/11/2014. See LICENSE.md
 */
@Slf4j
public class ReactiveInputs {
    public Observable<SingleInput> start(Observable.OnSubscribe<SingleInput> producer) {
        Observable<SingleInput> myObserved = Observable.create(producer);
        return myObserved;
    }

    @Data
    @Accessors(prefix = "m")
    public static class SingleInput {
        private final int mKeyCode;

        public SingleInput(int mKeyCode) {
            this.mKeyCode = mKeyCode;
        }
    }

    public static enum KEY_WRAPPER {
        UNKNOWN("?"), KEY_UP("Up"), KEY_DOWN("Down"), KEY_LEFT("Left"), KEY_RIGHT("Right"), KEY_ONE("One");
        @Getter
        private final String mKeyName;

        private KEY_WRAPPER(@NonNull String keyName) {
            mKeyName = keyName;
        }

        private static KEY_WRAPPER fromKeycode(int keyCode){
            KEY_WRAPPER[] values = values();
            if (keyCode < 0 || keyCode >= values.length){
                return UNKNOWN;
            }
            return values[keyCode];
        }
    }
}
