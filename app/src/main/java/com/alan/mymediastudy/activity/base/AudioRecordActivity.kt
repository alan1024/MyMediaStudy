package com.alan.mymediastudy.activity.base

import android.annotation.SuppressLint
import android.media.*
import android.util.Log
import com.alan.mymediastudy.activity.BaseActivity
import com.alan.mymediastudy.constant.Constants.AUDIO_FORMAT
import com.alan.mymediastudy.constant.Constants.CHANNEL_CONFIG
import com.alan.mymediastudy.constant.Constants.PATH_PCM
import com.alan.mymediastudy.constant.Constants.PATH_WAV
import com.alan.mymediastudy.constant.Constants.SAMPLE_RATE_INHZ
import com.alan.mymediastudy.databinding.ActivityAudioRecordBinding
import com.alan.mymediastudy.utils.PcmToWavUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import java.io.*


/**
 * @Auth: xujm
 * @Date: 2022/8/3
 * @Desc:
 * Android 音视频开发(二)：使用 AudioRecord 采集音频PCM并保存到文件
 * Android 音视频开发(三)：使用 AudioTrack 播放PCM音频
 */
class AudioRecordActivity : BaseActivity<ActivityAudioRecordBinding>() {
    private val TAG: String = "xujm"
    private val lifecycleScope = MainScope()
    private var audioRecord: AudioRecord? = null // 声明 AudioRecord 对象
    private var recordBufSize = 0 // 声明recoordBufffer的大小字段
    private var isRecording = false
    private var audioTrack: AudioTrack? = null
    private lateinit var audioData: ByteArray
    private var fileInputStream: FileInputStream? = null
    private var isStart: Boolean = false


    override fun bindViewBinding(): ActivityAudioRecordBinding {
        binding = ActivityAudioRecordBinding.inflate(layoutInflater)
        return binding
    }

    override fun init() {
        binding.btStart.setOnClickListener {
            startTime()
            startRecord()
        }
        binding.btStop.setOnClickListener {
            stopTime()
            stopRecord()
        }
        binding.btTrans.setOnClickListener {
            PcmToWavUtil(SAMPLE_RATE_INHZ, CHANNEL_CONFIG, AUDIO_FORMAT).pcmToWav(
                PATH_PCM,
                PATH_WAV
            )
        }
        binding.btPlay.setOnClickListener {
            startTime()
            playInModeStream()
        }
        binding.btPause.setOnClickListener {
            stopTime()
            stopPlay()
        }
        binding.btRing.setOnClickListener {
            startTime()
            playInModeStatic()
        }
    }


    @SuppressLint("MissingPermission")
    fun startRecord() {
        val minBufferSize =
            AudioRecord.getMinBufferSize(SAMPLE_RATE_INHZ, CHANNEL_CONFIG, AUDIO_FORMAT)
        audioRecord = AudioRecord(
            MediaRecorder.AudioSource.MIC, SAMPLE_RATE_INHZ,
            CHANNEL_CONFIG, AUDIO_FORMAT, minBufferSize
        )
        val data = ByteArray(minBufferSize)
        val file = File(PATH_PCM)
        if (!file.mkdirs()) {
            Log.e(TAG, "Directory not created")
        }
        if (file.exists()) {
            file.delete()
        }
        audioRecord!!.startRecording()
        isRecording = true

        // TODO: pcm数据无法直接播放，保存为WAV格式。
        Thread {
            var os: FileOutputStream? = null
            try {
                os = FileOutputStream(file)
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }
            if (null != os) {
                while (isRecording) {
                    val read = audioRecord!!.read(data, 0, minBufferSize)
                    // 如果读取音频数据没有出现错误，就将数据写入到文件
                    if (AudioRecord.ERROR_INVALID_OPERATION != read) {
                        try {
                            os.write(data)
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }
                try {
                    Log.i(TAG, "run: close file output stream !")
                    os.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }.start()
    }


    private fun stopRecord() {
        isRecording = false
        // 释放资源
        if (null != audioRecord) {
            audioRecord!!.stop()
            audioRecord!!.release()
            audioRecord = null
            //recordingThread = null;
        }
    }


    /**
     * 播放，使用stream模式
     */
    private fun playInModeStream() {
        /*
        * SAMPLE_RATE_INHZ 对应pcm音频的采样率
        * channelConfig 对应pcm音频的声道
        * AUDIO_FORMAT 对应pcm音频的格式
        * */
        val channelConfig: Int = AudioFormat.CHANNEL_OUT_MONO
        val minBufferSize =
            AudioTrack.getMinBufferSize(SAMPLE_RATE_INHZ, channelConfig, AUDIO_FORMAT)
        audioTrack = AudioTrack(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build(),
            AudioFormat.Builder().setSampleRate(SAMPLE_RATE_INHZ)
                .setEncoding(AUDIO_FORMAT)
                .setChannelMask(channelConfig)
                .build(),
            minBufferSize,
            AudioTrack.MODE_STREAM,
            AudioManager.AUDIO_SESSION_ID_GENERATE
        )
        audioTrack!!.play()
        val file = File(PATH_PCM)
        try {
            fileInputStream = FileInputStream(file)
            Thread {
                try {
                    val tempBuffer = ByteArray(minBufferSize)
                    while (fileInputStream!!.available() > 0) {
                        val readCount = fileInputStream!!.read(tempBuffer)
                        if (readCount == AudioTrack.ERROR_INVALID_OPERATION ||
                            readCount == AudioTrack.ERROR_BAD_VALUE
                        ) {
                            continue
                        }
                        if (readCount != 0 && readCount != -1) {
                            audioTrack!!.write(tempBuffer, 0, readCount)
                        }
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }.start()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }


    /**
     * 播放，使用static模式
     */
    private fun playInModeStatic() {
        // static模式，需要将音频数据一次性write到AudioTrack的内部缓冲区
        lifecycleScope.launch {
            val input: InputStream =
                resources.openRawResource(com.alan.mymediastudy.R.raw.ring)
            flow<Int> {
                try {
                    val out = ByteArrayOutputStream()
                    var b: Int
                    while (input.read().also { b = it } != -1) {
                        out.write(b)
                    }
                    Log.d(TAG, "Got the data")
                    audioData = out.toByteArray()
                } catch (e: IOException) {
                    Log.wtf(TAG, "Failed to read", e)
                } finally {
                    input.close()
                }
            }.flowOn(Dispatchers.IO)
                .collect {
                    Log.i(TAG, "Creating track...audioData.length = " + audioData.size)

                    // R.raw.ding铃声文件的相关属性为 22050Hz, 8-bit, Mono
                    audioTrack = AudioTrack(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .build(),
                        AudioFormat.Builder().setSampleRate(22050)
                            .setEncoding(AudioFormat.ENCODING_PCM_8BIT)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build(),
                        audioData.size,
                        AudioTrack.MODE_STATIC,
                        AudioManager.AUDIO_SESSION_ID_GENERATE
                    )
                    Log.d(TAG, "Writing audio data...")
                    audioTrack!!.write(audioData, 0, audioData.size)
                    Log.d(TAG, "Starting playback")
                    audioTrack!!.play()
                    Log.d(TAG, "Playing")
                }
        }

    }


    /**
     * 停止播放
     */
    private fun stopPlay() {
        if (audioTrack != null) {
            Log.d(TAG, "Stopping")
            audioTrack!!.stop()
            Log.d(TAG, "Releasing")
            audioTrack!!.release()
            Log.d(TAG, "Nulling")
        }
    }

    private fun startTime() {
        isStart = true
        var time = 0
        lifecycleScope.launch {
            flow<Int> {
                while (isStart) {
                    emit(time)
                    time++
                    delay(1000)
                }
            }.flowOn(Dispatchers.IO)
                .collect {
                    binding.tvName.text = "$time s"
                }
        }
    }

    private fun stopTime() {
        isStart = false
    }

}