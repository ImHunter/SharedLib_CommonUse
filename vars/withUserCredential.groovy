def call(String credID, Closure body) {

    def config = [:]
    body.resolveStrategy = Closure.OWNER_ONLY
    body.delegate = config

    def usName
    def usPwd

    this.withCredentials([[$class: 'UsernamePasswordMultiBinding', credentialsId: "${credID}",
                      usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD']]) {

        usName = envVar.USERNAME
        usPwd = envVar.PASSWORD ? envVar.PASSWORD : '\"\"'
    }

    body(usName, usPwd)
//    true

}
