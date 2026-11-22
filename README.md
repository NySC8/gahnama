<div align="center">

<img src="docs/logo.svg" width="112" alt="گاه‌نما">

# گاه‌نما · GAHNAMA

**تقویم روزانهٔ مینیمال برای اندروید**
شمسی · میلادی · قمری

[![build](https://img.shields.io/github/actions/workflow/status/NySC8/gahnama/build.yml?style=flat-square&color=285642&label=build)](https://github.com/NySC8/gahnama/actions)
[![release](https://img.shields.io/github/v/release/NySC8/gahnama?style=flat-square&color=285642&label=release)](https://github.com/NySC8/gahnama/releases/latest)
![android](https://img.shields.io/badge/Android-8%2B-47745B?style=flat-square)
![kotlin](https://img.shields.io/badge/Kotlin-Compose-47745B?style=flat-square)

[**⬇ دانلود APK**](https://github.com/NySC8/gahnama/releases/latest/download/gahnama.apk) · [**وب‌سایت**](https://gahnama.bipolarchat.ir)

</div>

<div dir="rtl">

## ویژگی‌ها
- نمایش تاریخ امروز با عدد بزرگ و روز هفته
- تقویم ماهانهٔ شمسی با انتخاب روز و تاریخ‌های متناظر
- تبدیل دقیق تاریخ با ICU خود اندروید (بدون الگوریتم دستی)
- راست‌چین کامل، Material 3، تم روشن و تاریک، فونت وزیرمتن

## ساخت
هر push به `main` با GitHub Actions یک APK می‌سازد و در [Releases](https://github.com/NySC8/gahnama/releases) منتشر می‌کند.

برای ساخت محلی، یک‌بار فونت را بگیرید (نیازمند npm) و پروژه را در Android Studio باز کنید (JDK 17):

</div>

```bash
bash scripts/fonts.sh
```

<div dir="rtl">

## ساختار
</div>

```
app/src/main/java/ir/gahnama/
  Dates.kt  Theme.kt  Home.kt  Calendar.kt  Screens.kt  MainActivity.kt
docs/        سایت (GitHub Pages)
scripts/     دریافت فونت
```

<div dir="rtl">

## نقشهٔ راه
- [x] رابط کاربری و سیستم تاریخ
- [ ] مناسبت‌ها و تعطیلات (JSON)
- [ ] تنظیمات ماندگار
- [ ] اعلان روزانه
- [ ] ویجت

فونت: [وزیرمتن](https://github.com/rastikerdar/vazirmatn) (OFL)

</div>
