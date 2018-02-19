package Energos.Jenkins.Common

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
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

    /**
     * Устанавливает значение текущего объекта из значения типа Date или Calendar
     * @param value Устанавливаемое значение
     */
    def setFromValue(def value) {
        if (value instanceof Date)
            setTime((value as Date).getTime())
        else if (value instanceof Calendar)
            setTime((value as Calendar).getTime().getTime())
        else if (value instanceof String || value instanceof  GString) {
            String strVal = value.toString()
            String matcher
            Date dateVal
//            DateTimeFormatter formatter = DateTimeFormatter.ofPattern('uuuuMMddhhmmss')
            DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                    .parseStrict()
                    .appendPattern("uuuuMMddhhmmss")
                    .toFormatter()
                    .withResolverStyle(ResolverStyle.STRICT)
//            try {
                LocalDateTime.parse(strVal, formatter)
//            } catch (e) {
//                formatter = DateTimeFormatter.ofPattern('yyyyMMdd')
//                try {
//                    dateVal = formatter.parse(strVal)
//                } catch (ee) {
//                    formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss".toString())
//                        dateVal = formatter.parse(strVal)
//                }
//            }
//            setFromValue(dateVal)
        } else
            new Exception("Непредусмотренный тип значения $value")
        this
    }

    private def setTimePart(int part, int value) {
        def c = toCalendar()
        c.set(part, value)
        this.setTime(c.getTime().getTime())
        this
    }

    def setTimeParts(Integer hr = null, Integer min = null, Integer sec = null){
        this.setFromValue(this.clearTime())
        if (hr!=null)
            setTimePart(Calendar.HOUR_OF_DAY, hr)
        if (min!=null)
            setTimePart(Calendar.MINUTE, min)
        if (sec!=null)
            setTimePart(Calendar.SECOND, sec)
        this
    }

    def setDateParts(Integer yy = null, Integer mm = null, Integer dd = null){
        if (yy!=null)
            setTimePart(Calendar.YEAR, yy)
        if (mm!=null)
            setTimePart(Calendar.MONTH, mm - 1)
        if (dd!=null)
            setTimePart(Calendar.DAY_OF_MONTH, dd)
        this
    }

    String formatFor1C(){
        boolean hasTime
        def c = this.toCalendar()
        hasTime = c.equals(c.clearTime())

        String fmtStr = hasTime ? 'yyyyMMddHHmmss' : 'yyyyMMdd'
        this.format(fmtStr)
    }

    String formatForXML(){
        String fmtStr = "yyyy-MM-dd'T'HH:mm:ss"
        this.format(fmtStr)
    }

    private static void withParseRange(String range, Closure body){
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
        def hh = null, mm, ss
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

    boolean inRange(String safeRangeFrom, String safeRangeTo){
        def fromDt = this.clone() as Date
        def toDt = this.clone() as Date
        withParseRange(safeRangeFrom){ int hh, int mm, int ss ->
            fromDt.setTimeParts(hh, mm, ss)
        }
        withParseRange(safeRangeTo){ int hh, int mm, int ss ->
            toDt.setTimeParts(hh, mm, ss)
        }
        if (fromDt>this)
            fromDt = fromDt.previous()
        def retVal = this>=fromDt && this<=toDt

        retVal
    }

    void addMinutes(int minutes) {
        Calendar c = this.toCalendar()
        c.add(Calendar.MINUTE, minutes)
        setFromValue(c)
    }

    DateNow getSafeDate(String safeRangeFrom, String safeRangeTo, Integer minutesDelta = null) {
        def retVal = this.clone() as DateNow
        if (retVal.inRange(safeRangeFrom, safeRangeTo)) {
            withParseRange(safeRangeFrom) {hh, mm, ss ->
                retVal.setTimeParts(hh, mm, ss)
                if (retVal.compareTo(this)>0)
                    --retVal
            }
        } else {
            retVal.addMinutes(minutesDelta)
        }
        retVal
    }

    def splitWith(Closure closure) {

        int yy, MM, dd, hh, mm, ss
        Calendar c = toCalendar()

        yy = c.get(Calendar.YEAR)
        MM = c.get(Calendar.MONTH) + 1
        dd = c.get(Calendar.DAY_OF_MONTH)
        hh = c.get(Calendar.HOUR_OF_DAY)
        mm = c.get(Calendar.MINUTE)
        ss = c.get(Calendar.SECOND)
        if (closure!=null)
            closure.call(yy, MM, dd, hh, mm, ss)

        [yy, MM, dd, hh, mm, ss]

    }

}