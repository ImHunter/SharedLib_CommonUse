package Energos.Jenkins.Common

import java.util.Date
import  java.util.Calendar

/**
 * Класс-утилита для манипуляций с датами.
 * Создан, чтобы инкапсулировать специфические для CI (в частности, для Деплойки) операции. И чтобы при этом не выходить из песочницы Jenkins.
 */
class DateNow extends Date{
    /**
     * Создаем объект типа Calendar на основании текущего значения объекта
     * @return Новый объект Calendar
     */
    Calendar toCalendar(){
        Calendar.getInstance().setTime(this)
    }

    String formatFor1C(boolean withTime = true){
        String fmtStr = withTime ? 'yyyyMMddHHmmss' : 'yyyyMMdd'
        this.format(fmtStr)
    }

    String formatToXML(boolean withTime = true){
        String fmtStr = withTime ? 'yyyy-MM-ddTHH:mm:ss' : 'yyyy-MM-dd'
        this.format(fmtStr)
    }

    private def getRangeFromStr(String range){
        boolean hasColon = range.contains(':')
        String[] parts
        if (hasColon)
            parts = range.split(':')
        else {
            ArrayList<String> lst = new ArrayList()
            String inStr = range
            int len
            while (!inStr.isEmpty()) {
                len = inStr.length()
                if (len>=2)
                    len = 2
                lst.add(inStr.substring(0, len))
                inStr = inStr.substring(len)
            }
            parts = lst.toArray()
        }
    }

    boolean inRange(String rangeFrom, String rangeTo){
        int hhFrom, mmFrom, ssFrom
        int hhTo, mmTo, ssTo

    }

}