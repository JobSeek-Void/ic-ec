package team.jsv.util_kotlin

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * [String]을 [Date]로 변환합니다.
 * @param [pattern] 날짜 및 시간 패턴
 * @param [locale] 날짜를 얻을 지역
 * @return [Date] */

fun String.toDate(pattern: String, locale: Locale = Locale.KOREA): Date {
    return try {
        val formatter = SimpleDateFormat(pattern, locale)
        formatter.parse(this)
    } catch (e: ParseException) {
        throw ParseException(e.message, e.errorOffset)
    }
}

/**
 * [Date]을 [String]로 변환합니다.
 * @param [pattern] 날짜 및 시간 패턴
 * @param [locale] 날짜를 얻을 지역
 * @return [String] */
fun Date.toFormatString(pattern: String, locale: Locale = Locale.KOREA): String {
    return try {
        val formatter = SimpleDateFormat(pattern, locale)
        formatter.format(this)
    } catch (e: ParseException) {
        throw ParseException(e.message, e.errorOffset)
    }
}