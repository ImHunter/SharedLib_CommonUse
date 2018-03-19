package Jenkins.Common

class Notifier {
    String address
    String subject
    String text
    Closure notifyHandler
    void doNotify(){
        notifyHandler?.call(this)
    }
}
