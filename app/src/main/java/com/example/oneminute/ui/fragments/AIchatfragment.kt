import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.oneminute.databinding.FragmentAiChatFragmentBinding
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.runBlocking
import com.example.oneminute.BuildConfig


class AIchatfragment : Fragment() {

    private var _binding: FragmentAiChatFragmentBinding? = null
    private val binding get() = _binding!!
    val initialPrompt = "Tu sei un giornalista esperto con una vasta conoscenza su eventi attuali, storia, scienza e cultura. Rispondi alle domande degli utenti come farebbe un giornalista professionale, fornendo informazioni accurate, contestualizzate e ben organizzate. Usa un tono chiaro e accessibile. Se la domanda riguarda un evento recente, cita la fonte di informazione più plausibile. Se non conosci la risposta a una domanda, spiega onestamente il motivo e suggerisci come l'utente potrebbe ottenere l'informazione."


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
                val response = generativeModel.generateContent(initialPrompt + inputText)
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