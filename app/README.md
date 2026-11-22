<p align="center"><img src="logo.svg" width="96" alt="گاه‌نما"></p>

# گاه‌نما (GAHNAMA)

تقویم روزانهٔ مینیمال برای اندروید: شمسی، میلادی و قمری. Kotlin · Jetpack Compose · Material 3 · MVVM.

## اجرا
پوشه را در Android Studio باز کنید (JDK 17، حداقل اندروید ۸) و Run بزنید.

## ساختار
```
app/src/main/java/ir/gahnama/
  Dates.kt     تبدیل تاریخ (ICU)
  Theme.kt     رنگ و تم
  Home.kt      صفحهٔ اصلی
  Calendar.kt  تقویم ماهانه
  Screens.kt   مناسبت‌ها، تنظیمات، درباره
  MainActivity.kt
```

## نقشهٔ راه
- [x] رابط کاربری و سیستم تاریخ
- [ ] مناسبت‌ها و تعطیلات (JSON)
- [ ] تنظیمات ماندگار
- [ ] اعلان روزانه
- [ ] ویجت
