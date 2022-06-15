#!groovy

class Global {
    static Object KUBERNETES = "docker-desktop"
    static Object REGISTRY = "oguzhandev"
    static Object REGISTRY_CREDENTIAL = "oguzhandev"
}

def Build(APP, DIR, BUILD_ID){

    env.REGISTRY_CREDENTIAL = Global.REGISTRY_CREDENTIAL
    env.REGISTRY = Global.REGISTRY

    env.APP = "${APP}"
    env.DIR = "${DIR}"
    env.BUILD_ID = "${BUILD_ID}"

     docker.withRegistry("https://registry.hub.docker.com", "${REGISTRY_CREDENTIAL}") {
        def IMG = docker.build("${REGISTRY}/${APP}", "-f ${DIR}/Dockerfile .")
        IMG.push("${BUILD_ID}")
    }
}

return this
