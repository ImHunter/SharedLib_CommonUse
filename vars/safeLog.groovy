import java.util.logging.*

def call(body) {

    def result
    def logger
    logger = new java.util.logging.Logger()

    // evaluate the body block, and collect configuration into the object
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    try {
        body(logger)
    } catch (e) {
        result = e
        currentBuild.result = 'FAILURE'
    }
    result
}