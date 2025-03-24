package com.kev.apptest12.ui.components.intro

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.kev.apptest12.R

class MyViewPagerAdapter : RecyclerView.Adapter<MyViewPagerAdapter.MyViewPagerViewHolder>() {
    inner class MyViewPagerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewPage: TextView = itemView.findViewById(R.id.tv_eachPage)
        val descriptiona: TextView = itemView.findViewById(R.id.descriptionTexto)

        val animacionLottie: LottieAnimationView = itemView.findViewById(R.id.animacionLottie)

     }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewPagerViewHolder {
        return MyViewPagerViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.layout_each_page,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: MyViewPagerViewHolder, position: Int) {
        when (position) {
            0 -> {
                holder.animacionLottie.setAnimation(R.raw.asd)
                holder.textViewPage.text = "CREA UN HABITO"

                holder.descriptiona.text = "El aprendizaje es un proceso continuo, siempre hay algo nuevo que descubrir y aprender. \n¡No dejes de lado la oportunidad de seguir creciendo!"

            }
            1 -> {
                holder.animacionLottie.setAnimation(R.raw.book)
                holder.textViewPage.text = "EXPLORA NUEVAS HABILIDADES"
                holder.descriptiona.text = "Diversas cursos y diplomados para diversas ciencias, todas las ramas de la educación en una misma app."

            }
            else -> {
                holder.animacionLottie.setAnimation(R.raw.capacitate)
                holder.textViewPage.text = "¡AYUDA A TU YO DEL FUTURO!"
                holder.descriptiona.text = "El éxito no llega de la noche a la mañana. \nEs el resultado del trabajo duro, la dedicación y la perseverancia"

            }
        }
    }

    override fun getItemCount(): Int = 3
}