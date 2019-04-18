/**
 * Возвращает значение ключа из переменных среды
 * @param envKey - Ключ (строка)
 * @param required - Требовать наличие ключа и его значения (Булево)
 * @return - Значение ключа
 */
def call(def envKey, def required = true){

    def res = env[envKey]
    if (res==null)
        echo("Ключ $envKey отсутствует или его значение не задано")
    if (required==true)
        assert res != null

    res

}