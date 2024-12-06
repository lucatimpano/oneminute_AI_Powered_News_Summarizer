package com.example.oneminute.ui.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.oneminute.R
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException


class AIchat_fragment : Fragment() {

    private val client = OkHttpClient()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_a_ichat_fragment, container, false)

        val etQuestion = view?.findViewById<EditText>(R.id.etQuestion)
        val btnInvio = view.findViewById<Button>(R.id.btnInvio)
        val answer = view?.findViewById<TextView>(R.id.RispostaAI)

        btnInvio.setOnClickListener {
            val question = etQuestion?.text.toString()
            Toast.makeText(requireContext(),question,Toast.LENGTH_SHORT).show()
            getResponse(question){response ->
                requireActivity().runOnUiThread {
                    answer?.text = response
                }
            }
        }


        return view
    }
    fun getResponse(question: String, callBack: (String) -> Unit) {
        val URL = "https://api.openai.com/v1/chat/completions"
        val API_KEY_GPT = "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"
        val requestBody = """
        {
            "model": "gpt-4o-mini",
            "messages": [
                {
                    "role": "system",
                    "content": "You are a helpful assistant."
                },
                {
                    "role": "user",
                    "content": "$question"
                }
            ]
        }
    """.trimIndent()


        val request = Request.Builder()
            .url(URL)
            .addHeader("Content-Type", "application/json")
            .addHeader("Authorization", "Bearer $API_KEY_GPT")
            .post(requestBody.toRequestBody("application/json".toMediaTypeOrNull()))
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("error", "API failed", e)
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string()
                if (body != null) {
                    try {
                        val jsonResponse = JSONObject(body)
                        val choicesArray = jsonResponse.getJSONArray("choices")
                        val firstChoice = choicesArray.getJSONObject(0)
                        val messageObject = firstChoice.getJSONObject("message")
                        val content = messageObject.getString("content")

                        callBack(content.trim())
                    } catch (e: Exception) {
                        Log.e("ResponseError", "Error parsing JSON response: ${e.message}", e)
                        // Handle the error, e.g., display an error message to the user
                        requireActivity().runOnUiThread {
                            Toast.makeText(
                                requireContext(),
                                "Error parsing JSON response",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                } else {
                    Log.v("Response", "Empty response from server")
                    // Handle empty response, e.g., display a message to the user
                    requireActivity().runOnUiThread {
                        Toast.makeText(
                            requireContext(),
                            "Empty response from server",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        })
    }

}