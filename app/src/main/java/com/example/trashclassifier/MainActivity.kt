package com.example.trashclassifier
import org.pytorch.LiteModuleLoader

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import org.pytorch.IValue
import org.pytorch.Module
import org.pytorch.torchvision.TensorImageUtils
import java.io.File
import java.io.FileOutputStream
import androidx.core.graphics.scale

class MainActivity : AppCompatActivity() {

    private lateinit var module: Module
    private val classes = arrayOf("cardboard", "glass", "metal", "paper", "plastic")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("MainActivity", "onCreate 起始")
        setContentView(R.layout.activity_main)

        val iv = findViewById<ImageView>(R.id.previewImage)
        val tv = findViewById<TextView>(R.id.resultText)
        Log.d("MainActivity", "UI 元件 iv=$iv, tv=$tv")

        val bitmap = obtainTestBitmap()
        iv.setImageBitmap(bitmap)
        Log.d("MainActivity", "已設定 ImageView 圖片")

        tv.text = "模型載入中..."
        Log.d("MainActivity", "已設定預設文字")

        Thread {
            try {
                module = LiteModuleLoader.load(assetFilePath())
                Log.d("MainActivity", "模型載入成功")
                val result = infer(bitmap)
                runOnUiThread {
                    tv.text = result
                    Log.d("MainActivity", "已顯示推論結果")
                }
            } catch (e: Exception) {
                Log.e("MainActivity", "模型載入或推論失敗", e)
                runOnUiThread {
                    tv.text = "模型載入失敗"
                }
            }
        }.start()
    }

    private fun infer(bitmap: Bitmap): String {
        val tensor = TensorImageUtils.bitmapToFloat32Tensor(
            bitmap,
            TensorImageUtils.TORCHVISION_NORM_MEAN_RGB,
            TensorImageUtils.TORCHVISION_NORM_STD_RGB
        )
        val output = module.forward(IValue.from(tensor)).toTensor().dataAsFloatArray
        val maxIdx = output.indices.maxByOrNull { output[it] } ?: -1
        val score = if (maxIdx >= 0) output[maxIdx] else 0f
        return "${classes.getOrNull(maxIdx) ?: "unknown"} （${"%.2f".format(score * 100)}%）"
    }

    private fun assetFilePath(): String {
        val assetName = "model_mobile.ptl"
        val file = File(filesDir, assetName)
        if (file.exists() && file.length() > 0) return file.absolutePath
        assets.open(assetName).use { input ->
            FileOutputStream(file).use { output ->
                val buffer = ByteArray(4 * 1024)
                var read: Int
                while (input.read(buffer).also { read = it } != -1) {
                    output.write(buffer, 0, read)
                }
                output.flush()
            }
        }
        return file.absolutePath
    }

    private fun obtainTestBitmap(): Bitmap {
        Log.d("MainActivity", "obtainTestBitmap called")
        val bmp = BitmapFactory.decodeResource(resources, R.drawable.test_img)
        return bmp.scale(224, 224)
    }
}
