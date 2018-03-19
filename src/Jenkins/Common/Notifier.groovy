package Jenkins.Common

class Notifier {
    String address
    String subject
    String body
    Closure notifyHandler
    void doNotify(){
        notifyHandler?.call(this)
    }

    Notifier setNotifyHandler(Closure handler) {
        notifyHandler = handler
        this
    }

}
