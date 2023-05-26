import { EmitterSubscription, LogBox, NativeEventEmitter, NativeModules } from 'react-native';
import { isObject, log, setDebugMode } from './util';
const { MobexModule } = NativeModules;
LogBox.ignoreLogs(['new NativeEventEmitter']);

const eventEmitter = new NativeEventEmitter(MobexModule);
const eventMap = {};

export const getRegisteredEventNames = () => {
  return Object.keys(eventMap);
};

export const removeEvent = (eventName: string) => {
  if (getRegisteredEventNames().includes(eventName)) {
    eventMap[eventName].remove();
    delete eventMap[eventName];
    log.d(`removeEvent: event ${eventName} is removed.`);
  } else {
    throw new Error(`${eventName} The event is not registered with react-native-locked`);
  }
};

export const getEventHandler = (eventName: string): EmitterSubscription => {
  return isObject(eventMap) && eventMap[eventName];
};

export const showLog = setDebugMode;

export const setEvent = (eventName: string, handler: (event: any) => void): EmitterSubscription => {
  if (getRegisteredEventNames().includes(eventName)) {
    log.d(`setEvent: event ${eventName} already registered.`);
    removeEvent(eventName);
  }
  eventMap[eventName] = eventEmitter.addListener(eventName, handler);
  return eventMap[eventName];
};

export const setEvents = (events) => {
  Object.entries(events).forEach(([k, v]) => setEvent(k, v));
};

export const isLocked = () => {
  return new Promise((resolve, reject) => {
    MobexModule.isScreenLocked((error, isLocked) => {
      if (error) {
        reject(error);
      } else {
        resolve(isLocked);
      }
    });
  });
};

export const killApp = (onBeforeExit) => {
  MobexModule.killApp(onBeforeExit);
};

export default MobexModule;
