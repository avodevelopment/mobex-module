export const log = {
    debugMode: true,
    
    d (...s) {
      this.debugMode && console.log('[LOCKED]', ...s);
    },
    w (...s) {
      this.debugMode && console.warn('[LOCKED]', ...s);
    },
    e (...s) {
      console.error('[LOCKED]', ...s);
    },
  }
  
  export const setDebugMode = (d: boolean) => {
    log.debugMode = d;
  };
  
  export const hasValue = (o) => {
    return o !== null && o !== undefined;
  };
  
  export const isEmpty = (o) => {
    return !hasValue(o);
  };
  
  export const tryCall = function (fn: Function, ...args) {
    return fn instanceof Function && fn.apply(this, args);
  };
  
  export const isObject = (o) => {
    return typeof o === "object";
  }
  