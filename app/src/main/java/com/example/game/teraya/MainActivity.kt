package com.example.game.teraya

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.game.teraya.databinding.ActivityMainBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.info.visibility =
            if (BuildConfig.DEBUG) View.VISIBLE
            else View.GONE

        binding.board.setGameOverCallBack(object : Board.GameOverCallBack {
            override fun gameOver(str: String) {
                MaterialAlertDialogBuilder(this@MainActivity).apply {
                    setTitle("Finished")
                    setMessage("winner is $str")
                    setPositiveButton("close", null)
                    setNeutralButton("one more round") { _, _ ->
                        binding.board.loadMap()
                        binding.playerO.visibility = View.VISIBLE
                        binding.playerX.visibility = View.VISIBLE
                    }
                    show()
                }

            }

            override fun changeView(str: String) {
                if (str == "X") {
                    binding.playerX.visibility = View.INVISIBLE
                    binding.playerO.visibility = View.VISIBLE
                } else {
                    binding.playerX.visibility = View.VISIBLE
                    binding.playerO.visibility = View.INVISIBLE
                }
            }

            override fun check(str: String) {
                binding.info.text = str
            }

        })
        binding.board.loadMap()

        binding.playerX.setOnClickListener {
            onClick(binding.playerX.text.toString())
        }
        binding.playerO.setOnClickListener {
            onClick(binding.playerO.text.toString())
        }
    }

    private fun onClick(str: String) {
        binding.board.inputText(str)
    }

}