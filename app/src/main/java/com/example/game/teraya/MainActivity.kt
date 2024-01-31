package com.example.game.teraya

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.game.teraya.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.board.setGameOverCallBack(object : Board.GameOverCallBack {
            override fun gameOver() {
                AlertDialog.Builder(this@MainActivity).setTitle("awesome!")
                    .setMessage("Congratulations，you solve the Sudoku!")
                    .setNegativeButton("exit") { dialog, which ->
                        dialog.dismiss()
                        finish()
                    }
                    .setPositiveButton("Next") { dialog, which ->
                        dialog.dismiss()

                    }
                    .create()
                    .show()

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