package Jenkins.Common

class Notifier {

    String address
    String subject
    String body
    def notifyHandler
    def owner

    void doNotify(){
        try {
            notifyHandler?.call(this)
        } catch (e) {
            owner?.echo(e.getMessage())
        }
    }

    Notifier setNotifyHandler(groovy.lang.Closure handler) {
        notifyHandler = handler
        this
    }

}
