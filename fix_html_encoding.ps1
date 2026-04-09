$files = Get-ChildItem -Path "src/main/resources/static" -Recurse -Filter *.html

$map = [ordered]@{
    "L�<ch" = "Lịch"
    "l�<ch" = "lịch"
    "�'" = "đ"
    "v�>i" = "với"
    "t�>i" = "tới"
    "m�>i" = "mới"
    "Đãnh giá" = "Đánh giá"
    "Gi�>i tênh" = "Giới tính"
    "Tu�.i" = "Tuổi"
    "Đ�<a chỉ" = "Địa chỉ"
    "D�<ch vụ" = "Dịch vụ"
    "d�<ch vụ" = "dịch vụ"
    "t�.ng" = "tổng"
    "xát nghiịm" = "xét nghiệm"
    "khêng" = "không"
    "Khêng" = "Không"
    "bu�Tc" = "buộc"
    "Gửi yáu cầu" = "Gửi yêu cầu"
    "hêng" = "hàng"
    "PHA'NG KHáM" = "PHÒNG KHÁM"
    "Lá Hoêng" = "Lê Hoàng"
    "Nguy�.n" = "Nguyễn"
    "V�fn" = "Văn"
    "Th�<" = "Thị"
    "N�Ti" = "Nội"
    "R�fng" = "Răng"
    "phêng" = "phòng"
    "cá" = "có"
    "th�f" = "thể"
    "lá" = "lý"
    "chuyên mên" = "chuyên môn"
    "b�<" = "bị"
    "hiịn" = "hiện"
    "Đ�Ti" = "Đội"
    "dịch vụ" = "dịch vụ"
    "Kh�ng" = "Không"
    "kh�ng" = "không"
    "c�" = "có"
    "th?t b?i" = "thất bại"
    "th�nh c�ng" = "thành công"
    "x�c nh?n" = "xác nhận"
    "b�c si" = "bác sĩ"
    "B�c si" = "Bác sĩ"
    "B�c sĩ" = "Bác sĩ"
    "chuy�n khoa" = "chuyên khoa"
    "Chuy�n khoa" = "Chuyên khoa"
    "d? li?u" = "dữ liệu"
    "D? li?u" = "Dữ liệu"
    "T�i kho?n" = "Tài khoản"
    "Cháo mừng" = "Chào mừng"
    "toên" = "toàn"
    "diịn" = "diện"
    "tai" = "tải"
    "gio" = "giờ"
    "chị" = "chỉ"
    "dY""�" = "📊"
    "dY'�" = "👥"
    "dY""." = "📅"
    "dY""^" = "📈"
    "dY""<" = "🧾"
    "dY��️" = "🧬"
    "dY��" = "🏥"
    "dYị" = "🫀"
    "dY'," = "👂"
    "dY'�️" = "👁️"
    "dY'�" = "👶"
    "�o,️" = "✂️"
    "dY�'ị��.️" = "🧑‍⚕️"
}

foreach ($f in $files) {
    $content = Get-Content -Raw -Path $f.FullName
    foreach ($k in $map.Keys) {
        $content = $content.Replace($k, $map[$k])
    }

    # Clean up the remaining replacement character from broken encoding.
    $content = $content.Replace("�", "")

    Set-Content -Path $f.FullName -Value $content -Encoding utf8
}
