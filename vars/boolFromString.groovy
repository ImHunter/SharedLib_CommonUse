def call(def strVal, Boolean defaultValue = false){

    def res;

    switch(strVal != null ? strVal.toString().toUpperCase() : '') {
        case ['Y', 'YES', '1', 'ДА', 'TRUE', 'ИСТИНА']: 
            res = true    
            break
        case ['N', 'NO', '0', 'НЕТ', 'FALSE', 'ЛОЖЬ']: 
            res = false    
            break
        default:
            res = defaultVal    
            break
    }
    res;

}