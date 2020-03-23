import Jenkins.Common.Folder
import Jenkins.Common.Notifier

/**
 * Функция-конструктор, создающая объект типа Notifier
 * @return Объект типа Notifier
 */
def call() {
    new Notifier()
}
