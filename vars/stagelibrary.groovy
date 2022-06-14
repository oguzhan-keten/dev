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

def Deploy(APP, DIR, NAMESPACE){

   env.KUBERNETES = Global.KUBERNETES
   env.REGISTRY_CREDENTIAL = Global.REGISTRY_CREDENTIAL

    env.APP = "${APP}"
    env.DIR = "${DIR}"
    env.NAMESPACE = "${NAMESPACE}"


    withCredentials([
        usernamePassword(credentialsId: "${REGISTRY_CREDENTIAL}", usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD'),
        file(credentialsId: "${KUBERNETES}", variable: 'KUBECONFIG')
    ]) {
        sh 'cat ${KUBECONFIG} > ~/.kube/config'
        sh 'kubectl -n ${NAMESPACE} create secret docker-registry dockerhub --docker-username=${USERNAME} --docker-password=${PASSWORD} --dry-run=client -o yaml | kubectl apply -f -'
        sh 'cat ${DIR}/k8s/prod/deployment.yaml | envsubst | kubectl -n ${NAMESPACE} apply -f -'
        sh 'kubectl -n ${NAMESPACE} apply -f ${DIR}/k8s/prod/service.yaml'
        sh 'kubectl -n ${NAMESPACE} apply -f ${DIR}/k8s/prod/configmap.yaml ||echo "ignoring non-configmap"'
        sh 'kubectl -n ${NAMESPACE} rollout status deployment ${APP} --timeout=3m'
    }
    
}

return this
