package com.anysoftkeyboard.resistance;
//Zbadać stany połączenia, by dobrze reagować na możliwości zadzwonienie, odebrania i nie odebrania połączenia, odfiltrowanie rozmów telefonicznych. Zbadać listenery
//1.  Wykres włączenia ekranu
//2. Odfiltorwanie połączeń (chociaż to może być jakoś tak zrobione, żeby na bieżąco elieminować
//3.  FILTR AR niech będzie. xn = suma czasów Ti / przez T
//4. Funkcja kary dla krótkich połączeń - jeśli Ti jest krótsze niż jakiś hmm... Parametr, może dziesięć sekund to Ti = Th, wartość parametru
//5. Progi co 0.2 - yn+1= yn*alfa + (1- alfa) xn;
public class ResistanceChanger {
    public int levelChanger(double decision, int resistanceDriver){

        if (decision > 0.5) {
            if (resistanceDriver != 4) {
                resistanceDriver++;
//                    sharedPreferences.edit().putBoolean(LEVEL_CHANGED, true).apply();
            }

        } else {
            if (resistanceDriver != 0) {
                resistanceDriver--;
//                    sharedPreferences.edit().putBoolean(LEVEL_CHANGED, true).apply();
            }
        }
//        if (decision > 0.50) {
//            if (resistanceDriver != 4)
//                resistanceDriver++;
//
//
//        }


        return  resistanceDriver;
    }
}
