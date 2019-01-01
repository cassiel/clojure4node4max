var path = require("path");
try {
    require("source-map-support").install();
} catch(err) {
}
require(path.join(path.resolve("."),"target/dev","goog","bootstrap","nodejs.js"));
require(path.join(path.resolve("."),"target/dev","cljs_deps.js"));
goog.global.CLOSURE_UNCOMPILED_DEFINES = {"cljs.core._STAR_target_STAR_":"nodejs"};
goog.require("net.cassiel.figwheel_test.core");
goog.require("cljs.nodejscli");
