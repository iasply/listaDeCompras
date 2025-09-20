package com.br.listadecompras

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import com.google.android.material.appbar.MaterialToolbar

class HelloFragment : Fragment(R.layout.fragment_hello) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Esconde a ActionBar global (só nessa tela)
        (requireActivity() as? androidx.appcompat.app.AppCompatActivity)?.supportActionBar?.hide()

        val toolbar = view.findViewById<MaterialToolbar>(R.id.toolbar)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Restaura ActionBar global ao sair do fragmento
        (requireActivity() as? androidx.appcompat.app.AppCompatActivity)?.supportActionBar?.show()
    }
}
