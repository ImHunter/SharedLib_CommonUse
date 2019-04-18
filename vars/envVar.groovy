/**
 * Возвращает значение ключа из переменных среды
 * @param envKey - Ключ (строка)
 * @param withAssert - Требовать наличие ключа и его значения (Булево)
 * @return - Значение ключа
 */
def call(def envKey, def withAssert = true){

    def res = env[envKey]
    if (withAssert==true)
        assert res != null

    res

}