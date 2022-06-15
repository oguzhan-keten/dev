#!groovy

class Global {
    static Object KUBERNETES = "docker-desktop"
    static Object REGISTRY = "nilveraltd"
    static Object REGISTRY_CREDENTIAL = "dockerhub"
}

def Build(APP, DIR, BUILD_ID){

    env.REGISTRY_CREDENTIAL = Global.REGISTRY_CREDENTIAL
    env.REGISTRY = Global.REGISTRY

    env.APP = "${APP}"
    env.DIR = "${DIR}"
    env.BUILD_ID = "${BUILD_ID}"

     docker.withRegistry("", "${REGISTRY_CREDENTIAL}") {
        def IMG = docker.build("${REGISTRY}/${APP}", "-f ${DIR}/Dockerfile .")
        IMG.push("${BUILD_ID}")
    }
}



return this
