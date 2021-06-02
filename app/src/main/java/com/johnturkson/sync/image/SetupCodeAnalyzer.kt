package com.johnturkson.sync.image

import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.Barcode
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import com.johnturkson.sync.data.Account
import java.net.URLDecoder

class SetupCodeAnalyzer(
    private val onSuccess: (Account) -> Unit,
) : ImageAnalysis.Analyzer {
    
    private val options = BarcodeScannerOptions.Builder()
        .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
        .build()
    
    @ExperimentalGetImage
    override fun analyze(proxy: ImageProxy) {
        val mediaImage = proxy.image
        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(mediaImage, proxy.imageInfo.rotationDegrees)
            val scanner = BarcodeScanning.getClient(options)
            scanner.process(image).addOnSuccessListener { barcodes ->
                val account = barcodes.firstOrNull()?.extractAccountInformation()
                if (account != null) onSuccess(account)
                proxy.close()
            }
        }
    }
    
    private fun Barcode.extractAccountInformation(): Account? {
        val text = URLDecoder.decode(displayValue, Charsets.UTF_8.toString())
        val pattern = "^otpauth://totp/[^:]+:(?<name>[^?]+)\\?secret=(?<secret>[^&]+)&issuer=(?<issuer>.+)$".toRegex()
        val match = pattern.matchEntire(text)
        val (name, secret, issuer) = match?.destructured ?: return null
        return Account(issuer, name, secret)
    }
}
