import Jenkins.Common.Folder
import Jenkins.Common.Notifier

def call(String address = null, String subject = null, String text = null, Closure notifyHandler = null) {
    new Notifier(address: address, subject: subject, text: text, notifyHandler: notifyHandler)
}
