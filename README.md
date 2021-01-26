# AR Завдання від SODD Warriors

Цей додаток ми створили на основі бібліотеки [BeyondAR](https://github.com/BeyondAR/beyondar) та proof-of-concept додатку [NavAR](https://github.com/KrishAmal/NavAR)

Хоча останнє оновлення фреймворку BeyondAR було ще у 2015 році, ця бібліотека не потребує підтримки девайсом ARCore - пропрієтарного фреймворку від гугл, який підтримує відносно мала кількість смарфонів.

В результаті ми оновили всі залежності, пофіксили кілька багів що призводили до поломки додатку, залишивши та покращивши лише необхідний нам функціонал.

Мінімальна версія android: 8.0 (Oreo). Девайс повинен мати компас, бути підключеним до мережі, та мати увімкнений GPS.


# Запуск додатку

Додаток написаний на чистій Java, тому запустити його можна за допомогою Android Studio, або будь-якого іншого середовища для розробки андроїд додатків.

Все що потрібно це склонувати репозиторій, завантажити всі залежності та запустити додаток на андроїд пристрої.

Або ще можна установити прекомпільовану версію: [Google Drive](https://drive.google.com/file/d/1bIGaX7BGXar-Yl7WwWgo8kfXk7_Duz5p/view?usp=sharing) (5mb)


## Утиліти та бібліотеки
* [Google Play Services](https://developers.google.com/android/guides/overview)
* [Android Map Utils](https://github.com/googlemaps/android-maps-utils)
* [Retrofit](https://github.com/square/retrofit)
* [ButterKnife](https://github.com/JakeWharton/butterknife)
* [Gson](https://github.com/google/gson)
* [BeyondAR](https://github.com/BeyondAR/beyondar)
* [NavAR](https://github.com/KrishAmal/NavAR)
