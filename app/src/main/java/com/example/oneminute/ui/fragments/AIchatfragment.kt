import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.oneminute.BuildConfig
import com.example.oneminute.databinding.FragmentAiChatFragmentBinding
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext


class AIchatfragment : Fragment() {

    private var _binding: FragmentAiChatFragmentBinding? = null
    private val binding get() = _binding!!
    val initialPrompt = "Tu sei un giornalista esperto con una vasta conoscenza su eventi attuali, storia, scienza e cultura. Rispondi alle domande degli utenti come farebbe un giornalista professionale, fornendo informazioni accurate, contestualizzate e ben organizzate. Usa un tono chiaro e accessibile. Se la domanda riguarda un evento recente, cita la fonte di informazione più plausibile. Se non conosci la risposta a una domanda, spiega onestamente il motivo e suggerisci come l'utente potrebbe ottenere l'informazione."
    private val generativeModel = GenerativeModel(
        // The Gemini 1.5 models are versatile and work with both text-only and multimodal prompts
        modelName = "gemini-1.5-flash",
        // Access your API key as a Build Configuration variable (see "Set up your API key" above)
        apiKey = BuildConfig.API_KEY
    )


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAiChatFragmentBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.promptButton.setOnClickListener {
            val inputText = binding.inputTextField.text.toString()
            if (inputText.isNotEmpty()) {
                generateResponse(initialPrompt + inputText)
            }else{
                Toast.makeText(requireContext(), "Inserisci una domanda", Toast.LENGTH_SHORT).show()
            }
            /***
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
            ***/
        }

        return view
    }

    private fun generateResponse(inputText: String) {
        // Mostra la ProgressBar
        binding.progressBar.visibility = View.VISIBLE
        binding.outputTextView.text = "Generazione in corso..." // Resetta il campo del risultato
        // Usa lifecycleScope per eseguire coroutine legate al ciclo di vita del Fragment
        lifecycleScope.launch {
            try {
                // Esegui la chiamata al modello in un thread di background
                val response = withContext(Dispatchers.IO) {
                    generativeModel.generateContent(initialPrompt + inputText)
                }
                // Aggiorna l'interfaccia utente con il risultato
                if(_binding != null){
                    binding.outputTextView.text = response.text
                }
            } catch (e: Exception) {
                if(_binding != null){
                    // Mostra un messaggio di errore
                    Toast.makeText(requireContext(), "Errore durante la generazione", Toast.LENGTH_SHORT).show()
                }
            } finally {
                if(_binding != null){
                    // Nascondi la ProgressBar
                    binding.progressBar.visibility = View.GONE
                }
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}