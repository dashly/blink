var __awaiter = (this && this.__awaiter) || function (thisArg, _arguments, P, generator) {
    function adopt(value) { return value instanceof P ? value : new P(function (resolve) { resolve(value); }); }
    return new (P || (P = Promise))(function (resolve, reject) {
        function fulfilled(value) { try { step(generator.next(value)); } catch (e) { reject(e); } }
        function rejected(value) { try { step(generator["throw"](value)); } catch (e) { reject(e); } }
        function step(result) { result.done ? resolve(result.value) : adopt(result.value).then(fulfilled, rejected); }
        step((generator = generator.apply(thisArg, _arguments || [])).next());
    });
};
import { WebPlugin } from '@capacitor/core';
export class DashlyBlinkWeb extends WebPlugin {
    constructor() {
        super({
            name: 'DashlyBlink',
            platforms: ['web']
        });
    }
    enableLocationSevices(_ssid, _password) {
        return __awaiter(this, void 0, void 0, function* () {
            const message = 'Web not supported';
            console.log(message);
            return { value: message };
        });
    }
    isLocationServicesEnabled(_ssid, _password) {
        return __awaiter(this, void 0, void 0, function* () {
            const message = 'Web not supported';
            console.log(message);
            return { value: false };
        });
    }
    getCurrentWifiSSID(_ssid, _password) {
        return __awaiter(this, void 0, void 0, function* () {
            const message = 'Web not supported';
            console.log(message);
            return { value: message };
        });
    }
    connectToMagnet(_ssid) {
        return __awaiter(this, void 0, void 0, function* () {
            const message = 'Web not supported';
            console.log(message);
            return { value: message };
        });
    }
    sendWifiLoginToMagnet(_ssid, _password) {
        return __awaiter(this, void 0, void 0, function* () {
            const message = 'Web not supported';
            console.log(message);
            return { value: message };
        });
    }
}
const DashlyBlink = new DashlyBlinkWeb();
export { DashlyBlink };
import { registerWebPlugin } from '@capacitor/core';
registerWebPlugin(DashlyBlink);
//# sourceMappingURL=web.js.map