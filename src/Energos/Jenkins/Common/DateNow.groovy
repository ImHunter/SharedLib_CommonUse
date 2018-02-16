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
        Calendar retVal = Calendar.getInstance()
        retVal.setTime(this)
        retVal
    }

    private def setTimePart(int part, int value) {
        def c = toCalendar()
        c.set(part, value)
        this.setTime(c.getTime().getTime())
        this
    }

    def setTimeParts(Integer hr = null, Integer min = null, Integer sec = null){
        if (hr!=null)
            setTimePart(Calendar.HOUR_OF_DAY, hr)
        if (min!=null)
            setTimePart(Calendar.MINUTE, min)
        if (sec!=null)
            setTimePart(Calendar.SECOND, sec)
        this
    }

    String formatFor1C(boolean withTime = true){
        String fmtStr = withTime ? 'yyyyMMddHHmmss' : 'yyyyMMdd'
        this.format(fmtStr)
    }

    String formatToXML(boolean withTime = true){
        String fmtStr = withTime ? 'yyyy-MM-ddTHH:mm:ss' : 'yyyy-MM-dd'
        this.format(fmtStr)
    }

    private static void withRangeFromStr(String range, Closure body){
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
        def hh, mm, ss
        if (parts.length>0)
            hh = Integer.valueOf(parts[0]).intValue()
        if (parts.length>1)
            mm = Integer.valueOf(parts[1]).intValue()
        else
            mm = 0
        if (parts.length>2)
            ss = Integer.valueOf(parts[2]).intValue()
        else
            ss = 0
        body.call(hh, mm, ss)
    }

    boolean inRange(String rangeFrom, String rangeTo){
        def fromDt = this.clone() as Date
        def toDt = this.clone() as Date
        withRangeFromStr(rangeFrom){int hh, int mm, int ss ->
            fromDt.setTimeParts(hh, mm, ss)
        }
        withRangeFromStr(rangeTo){int hh, int mm, int ss ->
            toDt.setTimeParts(hh, mm, ss)
        }
        if (fromDt>this)
            fromDt = fromDt.previous()
        def retVal = this>=fromDt && this<=toDt
        retVal
    }

}