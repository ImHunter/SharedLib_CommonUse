def call(String credID, Closure body) {

    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config

    def usName
    def usPwd

    withCredentials([[$class: 'UsernamePasswordMultiBinding', credentialsId: "${credID}",
                      usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD']]) {

        usName = env.USERNAME
        usPwd = env.PASSWORD ? env.PASSWORD : '\"\"'
    }

    body(usName, usPwd)
//    true

}
