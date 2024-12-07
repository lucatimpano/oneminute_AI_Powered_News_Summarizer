import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.oneminute.databinding.FragmentAiChatFragmentBinding // Assuming you're using View Binding
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.runBlocking
import com.example.oneminute.BuildConfig


class AIchatfragment : Fragment() {

    private var _binding: FragmentAiChatFragmentBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAiChatFragmentBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.promptButton.setOnClickListener {
            val inputText = binding.inputTextField.text.toString()

            val generativeModel = GenerativeModel(
                // The Gemini 1.5 models are versatile and work with both text-only and multimodal prompts
                modelName = "gemini-1.5-flash",
                // Access your API key as a Build Configuration variable (see "Set up your API key" above)
                apiKey = BuildConfig.API_KEY
            )
            runBlocking {
                val response = generativeModel.generateContent(inputText)
                binding.outputTextView.text = response.text
            }
        }
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}