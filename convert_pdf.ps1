try {
    $word = New-Object -ComObject Word.Application
    $word.Visible = $false
    Write-Host "Word COM available"
    $doc = $word.Documents.Open("C:\Campuscare\CampusCare_Project_Report.docx")
    $pdfPath = "C:\Campuscare\CampusCare_Project_Report.pdf"
    $doc.SaveAs([ref]$pdfPath, [ref]17)
    $doc.Close()
    $word.Quit()
    Copy-Item $pdfPath "C:\Users\DELL\Downloads\CampusCare_Project_Report.pdf" -Force
    Copy-Item $pdfPath "C:\Users\DELL\Desktop\CampusCare_Project_Report.pdf" -Force
    Write-Host "PDF generated successfully!"
} catch {
    Write-Host "Word COM error: " $_.Exception.Message
}
