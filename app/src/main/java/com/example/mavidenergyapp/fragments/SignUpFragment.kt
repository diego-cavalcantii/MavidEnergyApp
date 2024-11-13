package com.example.mavidenergyapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.mavidenergyapp.R
import com.example.mavidenergyapp.databinding.FragmentSignUpBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class SignUpFragment : Fragment() {

    private lateinit var auth: FirebaseAuth

    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!  // Safe property access

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.buttonSignUp.setOnClickListener {
            val email = binding.editTextEmailAddress.text.toString()
            val password = binding.editTextPassword.text.toString()
            val username = binding.editTextUserName.text.toString()

            lifecycleScope.launch {
                try {
                    val result = auth.createUserWithEmailAndPassword(email, password).await()

                    val currentUser = auth.currentUser
                    if (currentUser != null) {
                        val request = UserProfileChangeRequest.Builder()
                            .setDisplayName(username)
                            .build()
                        currentUser.updateProfile(request).await()

                        Toast.makeText(requireContext(), "Usuario criado", Toast.LENGTH_LONG).show()

                        findNavController().navigate(R.id.homeFragment)
                    } else {
                        Toast.makeText(requireContext(), "Erro ao criar usuário", Toast.LENGTH_LONG).show()
                    }
                } catch (ex: Exception) {
                    Toast.makeText(requireContext(), ex.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}