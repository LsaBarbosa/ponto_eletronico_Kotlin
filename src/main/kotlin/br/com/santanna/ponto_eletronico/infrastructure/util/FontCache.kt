package br.com.santanna.ponto_eletronico.infrastructure.util

import com.itextpdf.kernel.font.PdfFont
import com.itextpdf.kernel.font.PdfFontFactory
import java.util.concurrent.ConcurrentHashMap

object FontCache {
    private val cache = ConcurrentHashMap<String, PdfFont>()

    fun getFont(fontName: String): PdfFont {
        return cache.computeIfAbsent(fontName) { PdfFontFactory.createFont(it) }
    }
}