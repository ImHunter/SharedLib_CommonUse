/**
 * Функция для преобразования всяческих строковых значений в Булево
 * @param strVal 'Y', 'YES', '1', 'ДА', 'TRUE', 'ИСТИНА' - это true. 'N', 'NO', '0', 'НЕТ', 'FALSE', 'ЛОЖЬ' - это false
 * @param defaultValue
 * @return Результат преобразования, Булево
 */
def call(def strVal, Boolean defaultValue = false){

    def res

    switch(strVal != null ? strVal.toString().toUpperCase() : '') {
        case ['Y', 'YES', '1', 'ДА', 'TRUE', 'ИСТИНА']: 
            res = true    
            break
        case ['N', 'NO', '0', 'НЕТ', 'FALSE', 'ЛОЖЬ']: 
            res = false    
            break
        default:
            res = defaultValue
            break
    }
    res

}