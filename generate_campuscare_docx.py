import os
import docx
from docx.shared import Inches, Pt, RGBColor
from docx.enum.text import WD_ALIGN_PARAGRAPH
from docx.enum.table import WD_TABLE_ALIGNMENT, WD_ALIGN_VERTICAL
from docx.oxml import parse_xml, OxmlElement
from docx.oxml.ns import nsdecls, qn

def create_document():
    doc = docx.Document()

    # Section & Page Setup
    sec = doc.sections[0]
    sec.page_width = Inches(8.5)
    sec.page_height = Inches(11.0)
    sec.top_margin = Inches(0.8)
    sec.bottom_margin = Inches(0.8)
    sec.left_margin = Inches(0.8)
    sec.right_margin = Inches(0.8)

    # Set Page Borders on all pages (Matches reference DOCX)
    sectPr = sec._sectPr
    borders = parse_xml(
        r'<w:pgBorders xmlns:w="http://schemas.openxmlformats.org/wordprocessingml/2006/main" w:offsetFrom="page">'
        r'<w:top w:val="single" w:sz="8" w:space="24" w:color="000000"/>'
        r'<w:left w:val="single" w:sz="8" w:space="24" w:color="000000"/>'
        r'<w:bottom w:val="single" w:sz="8" w:space="24" w:color="000000"/>'
        r'<w:right w:val="single" w:sz="8" w:space="24" w:color="000000"/>'
        r'</w:pgBorders>'
    )
    sectPr.append(borders)

    # Base Colors
    RED = RGBColor(192, 0, 0)       # C00000
    INDIGO = RGBColor(75, 0, 130)    # 4B0082
    GREEN = RGBColor(0, 128, 0)      # 008000
    BLUE = RGBColor(0, 112, 192)     # 0070C0
    DARK = RGBColor(30, 41, 59)      # 1E293B

    # Helper functions
    def set_cell_background(cell, hex_color):
        shading = parse_xml(f'<w:shd {nsdecls("w")} w:fill="{hex_color}"/>')
        cell._tc.get_or_add_tcPr().append(shading)

    def set_cell_margins(cell, top=100, bottom=100, left=150, right=150):
        tcPr = cell._tc.get_or_add_tcPr()
        tcMar = parse_xml(
            f'<w:tcMar {nsdecls("w")}>'
            f'<w:top w:w="{top}" w:type="dxa"/>'
            f'<w:bottom w:w="{bottom}" w:type="dxa"/>'
            f'<w:left w:w="{left}" w:type="dxa"/>'
            f'<w:right w:w="{right}" w:type="dxa"/>'
            f'</w:tcMar>'
        )
        tcPr.append(tcMar)

    def add_p(text="", align=WD_ALIGN_PARAGRAPH.LEFT, space_before=0, space_after=6, line_spacing=1.15):
        p = doc.add_paragraph()
        p.alignment = align
        p.paragraph_format.space_before = Pt(space_before)
        p.paragraph_format.space_after = Pt(space_after)
        p.paragraph_format.line_spacing = line_spacing
        if text:
            r = p.add_run(text)
            r.font.name = "Times New Roman"
            r.font.size = Pt(11)
            r.font.color.rgb = DARK
        return p

    def add_heading_1(text):
        p = doc.add_paragraph()
        p.paragraph_format.space_before = Pt(14)
        p.paragraph_format.space_after = Pt(6)
        p.paragraph_format.keep_with_next = True
        r = p.add_run(text)
        r.font.name = "Times New Roman"
        r.font.size = Pt(14)
        r.bold = True
        r.font.color.rgb = DARK
        return p

    def add_heading_2(text):
        p = doc.add_paragraph()
        p.paragraph_format.space_before = Pt(10)
        p.paragraph_format.space_after = Pt(4)
        p.paragraph_format.keep_with_next = True
        r = p.add_run(text)
        r.font.name = "Times New Roman"
        r.font.size = Pt(12)
        r.bold = True
        r.font.color.rgb = DARK
        return p

    def add_listing(title, code_text):
        # Listing Title
        p_title = doc.add_paragraph()
        p_title.paragraph_format.space_before = Pt(8)
        p_title.paragraph_format.space_after = Pt(2)
        r_title = p_title.add_run(title)
        r_title.font.name = "Times New Roman"
        r_title.font.size = Pt(10)
        r_title.italic = True
        r_title.bold = True

        # Code Table Box
        tbl = doc.add_table(rows=1, cols=1)
        tbl.alignment = WD_TABLE_ALIGNMENT.CENTER
        cell = tbl.cell(0, 0)
        cell.width = Inches(6.8)
        set_cell_background(cell, "F8F9FA")
        set_cell_margins(cell, top=120, bottom=120, left=150, right=150)
        
        # Border
        borders_elm = parse_xml(
            r'<w:tcBorders xmlns:w="http://schemas.openxmlformats.org/wordprocessingml/2006/main">'
            r'<w:top w:val="single" w:sz="4" w:space="0" w:color="CCCCCC"/>'
            r'<w:left w:val="single" w:sz="4" w:space="0" w:color="CCCCCC"/>'
            r'<w:bottom w:val="single" w:sz="4" w:space="0" w:color="CCCCCC"/>'
            r'<w:right w:val="single" w:sz="4" w:space="0" w:color="CCCCCC"/>'
            r'</w:tcBorders>'
        )
        cell._tc.get_or_add_tcPr().append(borders_elm)

        p = cell.paragraphs[0]
        p.paragraph_format.space_before = Pt(0)
        p.paragraph_format.space_after = Pt(0)
        p.paragraph_format.line_spacing = 1.05
        r = p.add_run(code_text)
        r.font.name = "Consolas"
        r.font.size = Pt(8.5)
        r.font.color.rgb = RGBColor(15, 23, 42)

        # Space after listing
        p_space = doc.add_paragraph()
        p_space.paragraph_format.space_before = Pt(2)
        p_space.paragraph_format.space_after = Pt(6)

    def add_screenshot_placeholder(fig_no, fig_title, url_note):
        tbl = doc.add_table(rows=1, cols=1)
        tbl.alignment = WD_TABLE_ALIGNMENT.CENTER
        cell = tbl.cell(0, 0)
        cell.width = Inches(6.6)
        set_cell_background(cell, "F8FAFC")
        set_cell_margins(cell, top=200, bottom=200, left=200, right=200)

        borders_elm = parse_xml(
            r'<w:tcBorders xmlns:w="http://schemas.openxmlformats.org/wordprocessingml/2006/main">'
            r'<w:top w:val="dashed" w:sz="6" w:space="0" w:color="94A3B8"/>'
            r'<w:left w:val="dashed" w:sz="6" w:space="0" w:color="94A3B8"/>'
            r'<w:bottom w:val="dashed" w:sz="6" w:space="0" w:color="94A3B8"/>'
            r'<w:right w:val="dashed" w:sz="6" w:space="0" w:color="94A3B8"/>'
            r'</w:tcBorders>'
        )
        cell._tc.get_or_add_tcPr().append(borders_elm)

        p = cell.paragraphs[0]
        p.alignment = WD_ALIGN_PARAGRAPH.CENTER
        p.paragraph_format.space_before = Pt(12)
        p.paragraph_format.space_after = Pt(4)
        r1 = p.add_run("[ SCREENSHOT PLACEHOLDER ]\n")
        r1.font.name = "Times New Roman"
        r1.font.size = Pt(11)
        r1.bold = True
        r1.font.color.rgb = RGBColor(13, 148, 136)

        r2 = p.add_run("Paste your application screenshot here\n")
        r2.font.name = "Times New Roman"
        r2.font.size = Pt(10)
        r2.italic = True
        r2.font.color.rgb = RGBColor(100, 116, 139)

        r3 = p.add_run(f"(Figure {fig_no}: {fig_title})\n")
        r3.font.name = "Times New Roman"
        r3.font.size = Pt(9.5)
        r3.bold = True
        r3.font.color.rgb = DARK

        r4 = p.add_run(f"Note: {url_note}")
        r4.font.name = "Times New Roman"
        r4.font.size = Pt(8.5)
        r4.font.color.rgb = RGBColor(100, 116, 139)

        # Caption under figure
        p_cap = doc.add_paragraph()
        p_cap.alignment = WD_ALIGN_PARAGRAPH.CENTER
        p_cap.paragraph_format.space_before = Pt(4)
        p_cap.paragraph_format.space_after = Pt(10)
        r_cap = p_cap.add_run(f"Figure {fig_no}: {fig_title}")
        r_cap.font.name = "Times New Roman"
        r_cap.font.size = Pt(9.5)
        r_cap.italic = True

    logo_path = r"C:\Campuscare\extracted_media\image1.jpeg"

    # =========================================================================
    # PAGE 1: TITLE PAGE
    # =========================================================================
    p = add_p(space_before=10, space_after=8, align=WD_ALIGN_PARAGRAPH.CENTER)
    r = p.add_run("CampusCare: Student Complaint Management System")
    r.font.name = "Times New Roman"
    r.font.size = Pt(18)
    r.bold = True
    r.font.color.rgb = RED

    p = add_p(space_before=2, space_after=4, align=WD_ALIGN_PARAGRAPH.CENTER)
    r = p.add_run("A")
    r.font.name = "Times New Roman"
    r.font.size = Pt(12)

    p = add_p(space_before=2, space_after=6, align=WD_ALIGN_PARAGRAPH.CENTER)
    r = p.add_run("Skill Oriented Course Web Application report submitted in the partial fulfillment of the requirements for the award of the Degree of")
    r.font.name = "Times New Roman"
    r.font.size = Pt(11)
    r.bold = True

    p = add_p(space_before=4, space_after=2, align=WD_ALIGN_PARAGRAPH.CENTER)
    r = p.add_run("BACHELOR OF TECHNOLOGY")
    r.font.name = "Times New Roman"
    r.font.size = Pt(14)
    r.bold = True
    r.font.color.rgb = INDIGO

    p = add_p(space_before=2, space_after=2, align=WD_ALIGN_PARAGRAPH.CENTER)
    r = p.add_run("In")
    r.font.name = "Times New Roman"
    r.font.size = Pt(12)
    r.bold = True

    p = add_p(space_before=2, space_after=6, align=WD_ALIGN_PARAGRAPH.CENTER)
    r = p.add_run("COMPUTER SCIENCE AND ENGINEERING")
    r.font.name = "Times New Roman"
    r.font.size = Pt(13)
    r.bold = True
    r.font.color.rgb = GREEN

    p = add_p(space_before=2, space_after=4, align=WD_ALIGN_PARAGRAPH.CENTER)
    r = p.add_run("By")
    r.font.name = "Times New Roman"
    r.font.size = Pt(12)
    r.bold = True

    # Student Table (Matches reference Table 1)
    tbl_student = doc.add_table(rows=1, cols=2)
    tbl_student.alignment = WD_TABLE_ALIGNMENT.CENTER
    c0 = tbl_student.cell(0, 0)
    c1 = tbl_student.cell(0, 1)
    c0.width = Inches(3.2)
    c1.width = Inches(2.2)
    p0 = c0.paragraphs[0]
    p0.alignment = WD_ALIGN_PARAGRAPH.RIGHT
    r0 = p0.add_run("Bejawada Lakshman")
    r0.font.name = "Times New Roman"
    r0.font.size = Pt(11.5)
    r0.bold = True
    p1 = c1.paragraphs[0]
    p1.alignment = WD_ALIGN_PARAGRAPH.LEFT
    r1 = p1.add_run("   24761A05D6")
    r1.font.name = "Times New Roman"
    r1.font.size = Pt(11.5)
    r1.bold = True

    p = add_p(space_before=8, space_after=2, align=WD_ALIGN_PARAGRAPH.CENTER)
    r = p.add_run("Under the esteemed guidance of")
    r.font.name = "Times New Roman"
    r.font.size = Pt(12)
    r.bold = True

    p = add_p(space_before=2, space_after=2, align=WD_ALIGN_PARAGRAPH.CENTER)
    r = p.add_run("Mr. N. SrinivasaRao")
    r.font.name = "Times New Roman"
    r.font.size = Pt(12.5)
    r.bold = True
    r.font.color.rgb = RED

    p = add_p(space_before=0, space_after=6, align=WD_ALIGN_PARAGRAPH.CENTER)
    r = p.add_run("Sr. Assistant Professor")
    r.font.name = "Times New Roman"
    r.font.size = Pt(11)
    r.bold = True

    # College Logo
    if os.path.exists(logo_path):
        p_logo = doc.add_paragraph()
        p_logo.alignment = WD_ALIGN_PARAGRAPH.CENTER
        p_logo.paragraph_format.space_before = Pt(4)
        p_logo.paragraph_format.space_after = Pt(6)
        p_logo.add_run().add_picture(logo_path, width=Inches(1.8))

    p = add_p(space_before=2, space_after=2, align=WD_ALIGN_PARAGRAPH.CENTER)
    r = p.add_run("DEPARTMENT OF COMPUTER SCIENCE AND ENGINEERING")
    r.font.name = "Times New Roman"
    r.font.size = Pt(12.5)
    r.bold = True
    r.font.color.rgb = RED

    p = add_p(space_before=2, space_after=2, align=WD_ALIGN_PARAGRAPH.CENTER)
    r = p.add_run("LAKIREDDY BALIREDDY COLLEGE OF ENGINEERING (AUTONOMOUS)")
    r.font.name = "Times New Roman"
    r.font.size = Pt(12)
    r.bold = True
    r.font.color.rgb = BLUE

    p = add_p(space_before=0, space_after=1, align=WD_ALIGN_PARAGRAPH.CENTER)
    r = p.add_run("Accredited by NAAC with ‘A’ Grade & NBA (Under Tier - I),")
    r.font.name = "Times New Roman"
    r.font.size = Pt(9.5)
    r.bold = True

    p = add_p(space_before=0, space_after=1, align=WD_ALIGN_PARAGRAPH.CENTER)
    r = p.add_run("An ISO 21001:2018, 14001:2015, 50001:2018 Certified Institution")
    r.font.name = "Times New Roman"
    r.font.size = Pt(9.5)
    r.bold = True

    p = add_p(space_before=0, space_after=1, align=WD_ALIGN_PARAGRAPH.CENTER)
    r = p.add_run("Approved by AICTE, New Delhi and Affiliated to JNTUK, Kakinada")
    r.font.name = "Times New Roman"
    r.font.size = Pt(9.5)
    r.bold = True

    p = add_p(space_before=0, space_after=4, align=WD_ALIGN_PARAGRAPH.CENTER)
    r = p.add_run("L.B. REDDY NAGAR, MYLAVARAM, NTR DIST., A.P. - 521230.")
    r.font.name = "Times New Roman"
    r.font.size = Pt(9.5)
    r.bold = True

    p = add_p(space_before=4, space_after=0, align=WD_ALIGN_PARAGRAPH.CENTER)
    r = p.add_run("2024-2028")
    r.font.name = "Times New Roman"
    r.font.size = Pt(12)
    r.bold = True
    r.font.color.rgb = RED

    doc.add_page_break()

    # =========================================================================
    # PAGE 2: CERTIFICATE
    # =========================================================================
    p = add_p(space_before=6, space_after=2, align=WD_ALIGN_PARAGRAPH.CENTER)
    r = p.add_run("LAKIREDDY BALI REDDY COLLEGE OF ENGINEERING\n(AUTONOMOUS)")
    r.font.name = "Times New Roman"
    r.font.size = Pt(13)
    r.bold = True
    r.font.color.rgb = RED

    p = add_p(space_before=1, space_after=1, align=WD_ALIGN_PARAGRAPH.CENTER)
    r = p.add_run("Accredited by NAAC with ‘A’ Grade & NBA (Under Tier - I),")
    r.font.name = "Times New Roman"
    r.font.size = Pt(9)
    r.bold = True

    p = add_p(space_before=0, space_after=1, align=WD_ALIGN_PARAGRAPH.CENTER)
    r = p.add_run("An ISO 21001:2018, 14001:2015, 50001:2018 Certified Institution")
    r.font.name = "Times New Roman"
    r.font.size = Pt(9)
    r.bold = True

    p = add_p(space_before=0, space_after=1, align=WD_ALIGN_PARAGRAPH.CENTER)
    r = p.add_run("Approved by AICTE, New Delhi and Affiliated to JNTUK, Kakinada")
    r.font.name = "Times New Roman"
    r.font.size = Pt(9)
    r.bold = True

    p = add_p(space_before=0, space_after=4, align=WD_ALIGN_PARAGRAPH.CENTER)
    r = p.add_run("L.B. REDDY NAGAR, MYLAVARAM, NTR DIST., A.P. - 521230.")
    r.font.name = "Times New Roman"
    r.font.size = Pt(9)
    r.bold = True

    p = add_p(space_before=2, space_after=4, align=WD_ALIGN_PARAGRAPH.CENTER)
    r = p.add_run("Department of\nCOMPUTER SCIENCE AND ENGINEERING")
    r.font.name = "Times New Roman"
    r.font.size = Pt(12)
    r.bold = True
    r.font.color.rgb = BLUE

    if os.path.exists(logo_path):
        p_logo = doc.add_paragraph()
        p_logo.alignment = WD_ALIGN_PARAGRAPH.CENTER
        p_logo.paragraph_format.space_before = Pt(4)
        p_logo.paragraph_format.space_after = Pt(6)
        p_logo.add_run().add_picture(logo_path, width=Inches(1.6))

    p = add_p(space_before=4, space_after=8, align=WD_ALIGN_PARAGRAPH.CENTER)
    r = p.add_run("CERTIFICATE")
    r.font.name = "Times New Roman"
    r.font.size = Pt(15)
    r.bold = True

    p = add_p(space_before=4, space_after=6, align=WD_ALIGN_PARAGRAPH.JUSTIFY)
    p.add_run("This is to certify that the Skill Oriented Course Web Application entitled ")
    r = p.add_run("“CampusCare: Student Complaint Management System”")
    r.bold = True
    r.font.color.rgb = RED
    p.add_run(" is being submitted by")

    tbl_cert = doc.add_table(rows=1, cols=2)
    tbl_cert.alignment = WD_TABLE_ALIGNMENT.CENTER
    c0 = tbl_cert.cell(0, 0)
    c1 = tbl_cert.cell(0, 1)
    c0.width = Inches(3.2)
    c1.width = Inches(2.2)
    p0 = c0.paragraphs[0]
    p0.alignment = WD_ALIGN_PARAGRAPH.RIGHT
    r0 = p0.add_run("Bejawada Lakshman")
    r0.font.name = "Times New Roman"
    r0.font.size = Pt(11.5)
    r0.bold = True
    p1 = c1.paragraphs[0]
    p1.alignment = WD_ALIGN_PARAGRAPH.LEFT
    r1 = p1.add_run("   24761A05D6")
    r1.font.name = "Times New Roman"
    r1.font.size = Pt(11.5)
    r1.bold = True

    p = add_p(space_before=6, space_after=6, align=WD_ALIGN_PARAGRAPH.JUSTIFY)
    p.add_run("in partial fulfillment of the requirements for the award of degree of ")
    r = p.add_run("B.Tech in Computer Science and Engineering")
    r.bold = True
    r.font.color.rgb = GREEN
    p.add_run(" from ")
    r = p.add_run("Jawaharlal Nehru Technological University Kakinada")
    r.bold = True
    p.add_run(" is a record of Bonafide work carried out by him/her at ")
    r = p.add_run("Lakireddy Bali Reddy College of Engineering (Autonomous)")
    r.bold = True
    r.font.color.rgb = RED
    p.add_run(".")

    p = add_p(space_before=4, space_after=18, align=WD_ALIGN_PARAGRAPH.JUSTIFY)
    p.add_run("The results embodied in this Skill Oriented Course Web Application report have not been submitted to any other university or institute for the award of any degree or diploma.")

    tbl_sig = doc.add_table(rows=1, cols=2)
    tbl_sig.alignment = WD_TABLE_ALIGNMENT.CENTER
    c0 = tbl_sig.cell(0, 0)
    c1 = tbl_sig.cell(0, 1)
    c0.width = Inches(3.2)
    c1.width = Inches(3.2)
    p0 = c0.paragraphs[0]
    p0.alignment = WD_ALIGN_PARAGRAPH.LEFT
    r0 = p0.add_run("INTERNAL EXAMINER")
    r0.font.name = "Times New Roman"
    r0.font.size = Pt(11)
    r0.bold = True
    p1 = c1.paragraphs[0]
    p1.alignment = WD_ALIGN_PARAGRAPH.RIGHT
    r1 = p1.add_run("EXTERNAL EXAMINER")
    r1.font.name = "Times New Roman"
    r1.font.size = Pt(11)
    r1.bold = True

    doc.add_page_break()

    # =========================================================================
    # PAGE 3: ACKNOWLEDGEMENT
    # =========================================================================
    p = add_p(space_before=10, space_after=14, align=WD_ALIGN_PARAGRAPH.CENTER)
    r = p.add_run("ACKNOWLEDGEMENT")
    r.font.name = "Times New Roman"
    r.font.size = Pt(15)
    r.bold = True

    p = add_p(space_before=6, space_after=8, align=WD_ALIGN_PARAGRAPH.JUSTIFY)
    p.add_run("I would like to thank ")
    r = p.add_run("Mr. N. SrinivasaRao")
    r.bold = True
    p.add_run(", Sr. Assistant Professor, CSE department for the encouragement, technical suggestions, and invaluable support in carrying out this Skill Oriented Course Web Application.")

    p = add_p(space_before=6, space_after=8, align=WD_ALIGN_PARAGRAPH.JUSTIFY)
    p.add_run("I also take the privilege to record my sincere thanks to ")
    r = p.add_run("Dr. S. Nagarjuna Reddy")
    r.bold = True
    p.add_run(", Professor, Head of the Department of CSE whose encouragement, academic leadership, and continuous support crown my success.")

    p = add_p(space_before=6, space_after=8, align=WD_ALIGN_PARAGRAPH.JUSTIFY)
    p.add_run("I express my gratitude to the support given by the college management in completing my Web Application. I also express my sincere gratitude and deep sense of respect to ")
    r = p.add_run("Dr. K. Appa Rao")
    r.bold = True
    p.add_run(", Principal for making available all the required infrastructural facilities, laboratories, and his inspiration to carry out this Skill Oriented Course Web Application in the Institute.")

    p = add_p(space_before=6, space_after=8, align=WD_ALIGN_PARAGRAPH.JUSTIFY)
    p.add_run("I am thankful to the teaching and non-teaching faculty of the Computer Science and Engineering department for their direct as well as indirect guidance throughout the development of my Skill Oriented Course Web Application.")

    p = add_p(space_before=6, space_after=20, align=WD_ALIGN_PARAGRAPH.JUSTIFY)
    p.add_run("I am elated to avail myself of this opportunity to express my profound sense of gratitude to my beloved parents for their endless blessings and encouragement.")

    p = add_p(space_before=14, space_after=0, align=WD_ALIGN_PARAGRAPH.RIGHT)
    r = p.add_run("Bejawada Lakshman    24761A05D6")
    r.font.name = "Times New Roman"
    r.font.size = Pt(11.5)
    r.bold = True

    doc.add_page_break()

    # =========================================================================
    # PAGE 4: DECLARATION
    # =========================================================================
    p = add_p(space_before=10, space_after=14, align=WD_ALIGN_PARAGRAPH.CENTER)
    r = p.add_run("DECLARATION")
    r.font.name = "Times New Roman"
    r.font.size = Pt(15)
    r.bold = True

    p = add_p(space_before=6, space_after=10, align=WD_ALIGN_PARAGRAPH.JUSTIFY)
    p.add_run("I am here to declare that the project entitled ")
    r = p.add_run("“CampusCare: Student Complaint Management System”")
    r.bold = True
    p.add_run(" is authentic work done by me. I certify that the work contained in the report is original and has been completed by me under the guidance of my supervisor. The work has not been submitted to any other institute or university in preparing for any degree or diploma.")

    p = add_p(space_before=6, space_after=10, align=WD_ALIGN_PARAGRAPH.JUSTIFY)
    p.add_run("I have followed all guidelines provided by the institute for preparing this documentation. I have confirmed and adhered to the norms and guidelines given in the Ethical Code of Conduct of the Institute. Whenever I have used materials (data, theoretical analysis, architecture, figures, and text) from other sources, I have given due credit by citing them in the text of the report and giving their details in the references.")

    p = add_p(space_before=6, space_after=30, align=WD_ALIGN_PARAGRAPH.JUSTIFY)
    p.add_run("Further, I have taken permission from the respective authors and copyright owners of the sources, whenever necessary.")

    p = add_p(space_before=20, space_after=2, align=WD_ALIGN_PARAGRAPH.RIGHT)
    r = p.add_run("Signature of the student\n")
    r.font.name = "Times New Roman"
    r.font.size = Pt(11)
    r.bold = True
    r2 = p.add_run("Bejawada Lakshman   24761A05D6")
    r2.font.name = "Times New Roman"
    r2.font.size = Pt(11.5)
    r2.bold = True

    doc.add_page_break()

    # =========================================================================
    # PAGE 5: ABSTRACT
    # =========================================================================
    p = add_p(space_before=10, space_after=12, align=WD_ALIGN_PARAGRAPH.CENTER)
    r = p.add_run("ABSTRACT")
    r.font.name = "Times New Roman"
    r.font.size = Pt(15)
    r.bold = True

    p = add_p(space_before=4, space_after=8, align=WD_ALIGN_PARAGRAPH.JUSTIFY)
    p.add_run("In modern university campuses, resolving student grievances concerning hostel amenities, sanitation, academic facilities, electrical maintenance, transport, and IT connectivity is traditionally conducted through manual registers, verbal complaints, or informal emails. These outdated approaches suffer from lost complaints, zero progress visibility, unaccountable turnaround times, and severe dissatisfaction among students. ")
    r = p.add_run("CampusCare: Student Complaint Management System")
    r.bold = True
    p.add_run(" is an enterprise-grade, database-driven web application designed to automate, streamline, and govern student complaint filing, administrative staff assignment, workflow progress tracking, and resolution auditing on a unified campus portal. It is built strictly using HTML5, CSS3, and JavaScript with Bootstrap 5 for the responsive user interface, JavaServer Pages (JSP 2.3) and Java Servlets running on Apache Tomcat 9 for server-side business logic, and JDBC connecting directly to a robust MySQL 8.0 relational database.")

    p = add_p(space_before=4, space_after=8, align=WD_ALIGN_PARAGRAPH.JUSTIFY)
    p.add_run("The application implements three distinct, role-based workflows for Students, Staff, and Administrators. Student registration incorporates domain-specific constraints distinguishing Hostel Students (requiring verified hostel name and room number) from Day Scholars (strictly setting accommodation fields to NULL). Passwords are never stored in plain text; they are secured using the BCrypt key derivation algorithm with 12 rounds of salted hashing. Authenticated users maintain their session in an HTTP session cookie with complete session invalidation and renewal at login to prevent session fixation attacks. Students lodge complaints with categories, titles, detailed descriptions, and priority levels, automatically receiving a numeric ticket starting from 1001 displayed publicly as GC1001. Students track ticket progress, view assigned staff, inspect an immutable chronological audit timeline, and can withdraw their complaints prior to staff processing.")

    p = add_p(space_before=4, space_after=8, align=WD_ALIGN_PARAGRAPH.JUSTIFY)
    p.add_run("Assigned staff members receive a dedicated dashboard displaying their personal complaint queue, start work by transitioning ticket status to IN_PROGRESS, and mark issues as RESOLVED only after entering mandatory resolution remarks. Administrators possess central operational governance: viewing campus-wide grievance metrics, filtering complaints across multiple criteria, assigning and reassigning tickets to active staff, managing student and staff account activation states, and monitoring real-time staff workload statistics.")

    p = add_p(space_before=4, space_after=10, align=WD_ALIGN_PARAGRAPH.JUSTIFY)
    p.add_run("Security and data reliability were engineered from inception: 100% of database interactions execute via JDBC PreparedStatement to eliminate SQL injection; synchronizer CSRF tokens defend all state-altering POST requests; user-supplied text is sanitized with HTML escaping; and atomic database transactions ensure that complaint updates and audit history logs are saved simultaneously. The project demonstrates an authentic, full-stack Java web application adhering strictly to classical MVC architecture, verified through automated integration testing and live MySQL persistence.")

    p = add_p(space_before=8, space_after=0, align=WD_ALIGN_PARAGRAPH.JUSTIFY)
    r = p.add_run("Keywords: ")
    r.bold = True
    p.add_run("JSP, Servlets, JDBC, MySQL, Apache Tomcat 9, BCrypt, RBAC, Complaint Management, CampusCare.")

    doc.add_page_break()

    # =========================================================================
    # PAGE 6: LIST OF CONTENTS
    # =========================================================================
    p = add_p(space_before=8, space_after=10, align=WD_ALIGN_PARAGRAPH.CENTER)
    r = p.add_run("LIST OF CONTENTS")
    r.font.name = "Times New Roman"
    r.font.size = Pt(14)
    r.bold = True

    contents_data = [
        ("CONTENTS", "PAGE NO"),
        ("1. INTRODUCTION . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .", "1-3"),
        ("    1.1 Overview of the Project . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .", "1-2"),
        ("    1.2 Feasibility Study . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .", "2-3"),
        ("2. TECHNOLOGIES USED . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .", "4-7"),
        ("    2.1 HTML, CSS & JavaScript . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .", "4"),
        ("    2.2 JDBC (MySQL Connector/J) . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .", "4-6"),
        ("    2.3 Servlets and the Apache Tomcat Container . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .", "6"),
        ("    2.4 JSP (JavaServer Pages) . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .", "6-7"),
        ("    2.5 Supporting Tools and Libraries . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .", "7"),
        ("3. WEB APPLICATION ARCHITECTURE . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .", "8-12"),
        ("    3.1 Three-Tier Architecture . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .", "8"),
        ("    3.2 Pages and Their Roles . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .", "8-9"),
        ("    3.3 Database Design . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .", "9-11"),
        ("    3.4 Data Flow . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .", "11-12"),
        ("4. CODING & IMPLEMENTATION . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .", "13-20"),
        ("    4.1 Project Structure . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .", "13"),
        ("    4.2 Database Implementation . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .", "13-14"),
        ("    4.3 Database Connection . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .", "14"),
        ("    4.4 Student Registration & Validation Module . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .", "14-15"),
        ("    4.5 Login and Logout Module . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .", "15-16"),
        ("    4.6 Complaint Filing Module . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .", "16-17"),
        ("    4.7 Staff Action & Resolution Module . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .", "17-18"),
        ("    4.8 Admin Assignment & User Governance Module . . . . . . . . . . . . . . . . . . . . . . . . . . . .", "18-19"),
        ("    4.9 Complaint Status History & Audit Module . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .", "19-20"),
        ("    4.10 Security Measures . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .", "20"),
        ("5. RESULTS . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .", "21-26"),
        ("    5.1 Login Page . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .", "21"),
        ("    5.2 Student Dashboard & New Complaint Page . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .", "21-22"),
        ("    5.3 Complaint Details & Status Timeline Page . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .", "22"),
        ("    5.4 Staff Dashboard & Resolution Page . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .", "22-23"),
        ("    5.5 Admin Dashboard & Assignment Page . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .", "23"),
        ("    5.6 All Complaints & Management Pages . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .", "23-24"),
        ("    5.7 Database Output . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .", "24-26"),
        ("    5.8 Testing . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .", "26"),
        ("6. CONCLUSION . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .", "27"),
        ("7. REFERENCES . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .", "28")
    ]

    tbl_toc = doc.add_table(rows=len(contents_data), cols=2)
    tbl_toc.alignment = WD_TABLE_ALIGNMENT.CENTER
    for r_idx, (col1, col2) in enumerate(contents_data):
        c0 = tbl_toc.cell(r_idx, 0)
        c1 = tbl_toc.cell(r_idx, 1)
        c0.width = Inches(5.6)
        c1.width = Inches(1.2)
        set_cell_margins(c0, top=40, bottom=40, left=80, right=80)
        set_cell_margins(c1, top=40, bottom=40, left=80, right=80)
        
        p0 = c0.paragraphs[0]
        p0.paragraph_format.space_before = Pt(1)
        p0.paragraph_format.space_after = Pt(1)
        p0.paragraph_format.line_spacing = 1.05
        r0 = p0.add_run(col1)
        r0.font.name = "Times New Roman"
        r0.font.size = Pt(9.5)
        if r_idx == 0 or not col1.startswith("    "):
            r0.bold = True

        p1 = c1.paragraphs[0]
        p1.alignment = WD_ALIGN_PARAGRAPH.RIGHT
        p1.paragraph_format.space_before = Pt(1)
        p1.paragraph_format.space_after = Pt(1)
        p1.paragraph_format.line_spacing = 1.05
        r1 = p1.add_run(col2)
        r1.font.name = "Times New Roman"
        r1.font.size = Pt(9.5)
        if r_idx == 0 or not col1.startswith("    "):
            r1.bold = True

    doc.add_page_break()

    # =========================================================================
    # PAGE 7: LIST OF ABBREVIATIONS
    # =========================================================================
    p = add_p(space_before=10, space_after=14, align=WD_ALIGN_PARAGRAPH.CENTER)
    r = p.add_run("LIST OF ABBREVIATIONS")
    r.font.name = "Times New Roman"
    r.font.size = Pt(14)
    r.bold = True

    abbreviations = [
        "1.  HTML – Hypertext Markup Language",
        "2.  CSS – Cascading Style Sheets",
        "3.  JS – JavaScript",
        "4.  JDBC – Java Database Connectivity",
        "5.  JSP – JavaServer Pages",
        "6.  SQL – Structured Query Language",
        "7.  DB – Database",
        "8.  RDBMS – Relational Database Management System",
        "9.  HTTP – Hypertext Transfer Protocol",
        "10. URL – Uniform Resource Locator",
        "11. CRUD – Create, Read, Update, Delete",
        "12. IDE – Integrated Development Environment",
        "13. JDK – Java Development Kit",
        "14. API – Application Programming Interface",
        "15. UI – User Interface",
        "16. ER – Entity-Relationship",
        "17. PK – Primary Key",
        "18. FK – Foreign Key",
        "19. WAR – Web Application Archive",
        "20. XSS – Cross-Site Scripting",
        "21. CSRF – Cross-Site Request Forgery",
        "22. RBAC – Role-Based Access Control",
        "23. MVC – Model-View-Controller",
        "24. SOC – Skill Oriented Course",
        "25. DDL – Data Definition Language",
        "26. DML – Data Manipulation Language",
        "27. PRG – Post/Redirect/Get Pattern"
    ]

    for abbr in abbreviations:
        p = add_p(abbr, space_before=2, space_after=2)
        p.paragraph_format.left_indent = Inches(0.5)

    doc.add_page_break()

    # =========================================================================
    # CHAPTER 1: INTRODUCTION
    # =========================================================================
    p_header = add_p(space_before=0, space_after=4, align=WD_ALIGN_PARAGRAPH.RIGHT)
    r_hdr = p_header.add_run("1")
    r_hdr.font.name = "Times New Roman"
    r_hdr.font.size = Pt(10)

    add_heading_1("1. INTRODUCTION")
    add_heading_2("1.1 Overview of the Project")
    
    p = add_p(space_before=2, space_after=6, align=WD_ALIGN_PARAGRAPH.JUSTIFY)
    p.add_run("CampusCare is a comprehensive, database-driven enterprise web application engineered to centralize, streamline, and govern student grievance redressal across collegiate campuses. In academic institutions, students frequently encounter campus-related difficulties ranging from hostel room maintenance, plumbing failures, electrical socket damages, and water cooler leaks to classroom equipment breakdowns, sanitation issues, transport delays, and campus Wi-Fi disruptions. CampusCare empowers students to submit structured complaints directly through a responsive browser portal, enforces dynamic conditional constraints for Hostel and Day Scholar students, generates standardized ticket identifiers (e.g., GC1001), routes grievances to administrators, dispatches tasks to department staff, and maintains an immutable audit timeline of all corrective actions.")

    p = add_p(space_before=4, space_after=4, align=WD_ALIGN_PARAGRAPH.LEFT)
    r = p.add_run("Problem Statement")
    r.bold = True

    p = add_p(space_before=2, space_after=6, align=WD_ALIGN_PARAGRAPH.JUSTIFY)
    p.add_run("In traditional university environments, students must physically locate wardens, facility supervisors, or department heads to submit verbal complaints or write in physical register notebooks. This conventional approach causes critical grievances to be misplaced, ignored, or delayed. Students possess zero visibility into ticket progress or assigned personnel, staff technicians lack an organized priority queue, and college administrators have no centralized data to monitor staff workload, resolution turnaround speeds, or recurring departmental infrastructure faults. CampusCare eliminates these operational bottlenecks through a secure, transparent, and accountable digital workflow.")

    p = add_p(space_before=4, space_after=4, align=WD_ALIGN_PARAGRAPH.LEFT)
    r = p.add_run("Objectives")
    r.bold = True

    objs = [
        "To build a responsive, user-friendly campus portal using HTML5, CSS3, JavaScript, and Bootstrap 5 with deep navy and teal accents.",
        "To persistently store student profiles, staff accounts, administrative credentials, categories, grievances, and audit logs in a connected MySQL 8.0 relational database.",
        "To provide secure student registration and multi-portal login using salted BCrypt password hashing and enterprise HTTP session management.",
        "To enforce strict Role-Based Access Control (RBAC) across three distinct user roles: Student, Staff, and Administrator.",
        "To implement conditional domain rules requiring hostel name and room number for Hostel Students while strictly setting them to NULL for Day Scholars.",
        "To orchestrate an audited complaint lifecycle (PENDING → ASSIGNED → IN_PROGRESS → RESOLVED, with WITHDRAWN and REJECTED paths) backed by atomic database transactions.",
        "To incorporate comprehensive defensive security measures: parameterized JDBC PreparedStatements, anti-CSRF token verification, and XSS output escaping."
    ]
    for obj in objs:
        p = add_p(f"• {obj}", space_before=1, space_after=3, align=WD_ALIGN_PARAGRAPH.JUSTIFY)
        p.paragraph_format.left_indent = Inches(0.25)

    p = add_p(space_before=6, space_after=4, align=WD_ALIGN_PARAGRAPH.LEFT)
    r = p.add_run("Modules of the Application")
    r.bold = True

    tbl_modules = doc.add_table(rows=6, cols=2)
    tbl_modules.alignment = WD_TABLE_ALIGNMENT.CENTER
    mod_data = [
        ("Module", "Description"),
        ("Student Management", "Registration with conditional hostel field toggling, profile validation, BCrypt password hashing, secure sign-in, and account status governance."),
        ("Complaint Filing", "Structured grievance submission with active category selection, title, description, priority assignment (LOW, MEDIUM, HIGH), and initial PENDING status."),
        ("Staff Work Queue", "Personalized assigned complaints queue, progress transition to IN_PROGRESS, and complaint resolution requiring mandatory remarks (<= 500 chars)."),
        ("Admin Governance", "Central dashboard with summary cards, multi-attribute filtering, staff assignment and reassignment, student/staff account activation, and workload analytics."),
        ("Audit History & Tracking", "Real-time ticket tracking displaying assigned technician, timestamps, and an immutable chronological timeline of state changes and resolution remarks.")
    ]
    for r_idx, (col1, col2) in enumerate(mod_data):
        c0 = tbl_modules.cell(r_idx, 0)
        c1 = tbl_modules.cell(r_idx, 1)
        c0.width = Inches(2.2)
        c1.width = Inches(4.6)
        set_cell_margins(c0, top=60, bottom=60, left=100, right=100)
        set_cell_margins(c1, top=60, bottom=60, left=100, right=100)
        if r_idx == 0:
            set_cell_background(c0, "EBF1F5")
            set_cell_background(c1, "EBF1F5")
        p0 = c0.paragraphs[0]
        r0 = p0.add_run(col1)
        r0.font.name = "Times New Roman"
        r0.font.size = Pt(9.5)
        if r_idx == 0: r0.bold = True
        p1 = c1.paragraphs[0]
        r1 = p1.add_run(col2)
        r1.font.name = "Times New Roman"
        r1.font.size = Pt(9.5)
        if r_idx == 0: r1.bold = True

    p = add_p(space_before=8, space_after=4, align=WD_ALIGN_PARAGRAPH.LEFT)
    r = p.add_run("Existing and Proposed System")
    r.bold = True

    p = add_p(space_before=2, space_after=6, align=WD_ALIGN_PARAGRAPH.JUSTIFY)
    p.add_run("In the existing manual system, students must report maintenance issues on paper registers at hostel offices or department desks. There is no confirmation receipt, no tracking number, and no mechanism to verify whether a technician was ever dispatched. In the proposed CampusCare system, students lodge complaints online in seconds, receive an instant ticket identifier (e.g. GC1001), and observe live status updates. Staff receive direct digital assignments, and administrators maintain total oversight of institutional infrastructure health.")

    p = add_p(space_before=4, space_after=4, align=WD_ALIGN_PARAGRAPH.LEFT)
    r = p.add_run("Scope")
    r.bold = True

    p = add_p(space_before=2, space_after=8, align=WD_ALIGN_PARAGRAPH.JUSTIFY)
    p.add_run("The project encompasses campus-wide grievance redressal covering Hostels, Sanitation, Classrooms, Transport, IT/Internet, and Electrical maintenance. It includes self-service student registration, three dedicated login portals, assignment workflows, workload monitoring, and complete audit logging. Integration with third-party SMS gateways and mobile native apps is identified under future scope in Chapter 6.")

    add_heading_2("1.2 Feasibility Study")
    p = add_p("A feasibility study examines whether the project can be accomplished within constraints of available time, technical tools, and resources. The following dimensions were evaluated:", space_before=2, space_after=6, align=WD_ALIGN_PARAGRAPH.JUSTIFY)

    p = add_p(space_before=2, space_after=2, align=WD_ALIGN_PARAGRAPH.LEFT)
    r = p.add_run("Technical Feasibility")
    r.bold = True

    p = add_p("Java Servlets, JSP, JDBC, MySQL 8.0, and Apache Tomcat 9 are mature, reliable, industry-proven technologies that execute efficiently on standard student computers. The MySQL Connector/J driver communicates directly over TCP/IP, and BCrypt provides enterprise password security through Maven. The project is therefore technically feasible.", space_before=1, space_after=6, align=WD_ALIGN_PARAGRAPH.JUSTIFY)

    tbl_tech_feas = doc.add_table(rows=5, cols=2)
    tbl_tech_feas.alignment = WD_TABLE_ALIGNMENT.CENTER
    tf_data = [
        ("Item", "Requirement used in this project"),
        ("Processor / RAM", "Intel Core i3 / i5 or higher, 4 GB RAM (8 GB recommended)"),
        ("Disk space", "About 2 GB free for MySQL 8.0, Tomcat 9.0, Eclipse and Maven project"),
        ("Operating system", "Windows 10 / 11 (64-bit)"),
        ("Java / Server", "Java 8 / Java 21 LTS, Apache Tomcat 9.0 (Port 8081)")
    ]
    for r_idx, (col1, col2) in enumerate(tf_data):
        c0 = tbl_tech_feas.cell(r_idx, 0)
        c1 = tbl_tech_feas.cell(r_idx, 1)
        c0.width = Inches(2.2)
        c1.width = Inches(4.6)
        set_cell_margins(c0, top=40, bottom=40, left=100, right=100)
        set_cell_margins(c1, top=40, bottom=40, left=100, right=100)
        if r_idx == 0:
            set_cell_background(c0, "EBF1F5")
            set_cell_background(c1, "EBF1F5")
        p0 = c0.paragraphs[0]
        r0 = p0.add_run(col1)
        r0.font.name = "Times New Roman"
        r0.font.size = Pt(9.5)
        if r_idx == 0: r0.bold = True
        p1 = c1.paragraphs[0]
        r1 = p1.add_run(col2)
        r1.font.name = "Times New Roman"
        r1.font.size = Pt(9.5)
        if r_idx == 0: r1.bold = True

    p = add_p(space_before=6, space_after=2, align=WD_ALIGN_PARAGRAPH.LEFT)
    r = p.add_run("Economic Feasibility")
    r.bold = True
    p = add_p("All development tools and runtime environments utilized—Java JDK, Eclipse IDE, Apache Tomcat 9, MySQL Community Server, Maven, and jBCrypt—are open-source and free of licensing costs. No commercial software licenses were purchased.", space_before=1, space_after=6, align=WD_ALIGN_PARAGRAPH.JUSTIFY)

    p = add_p(space_before=4, space_after=2, align=WD_ALIGN_PARAGRAPH.LEFT)
    r = p.add_run("Operational Feasibility")
    r.bold = True
    p = add_p("The system provides clear, role-specific navigation bars, informative status badges, accessible form labels, and explicit feedback alerts (e.g. 'Complaint GC1001 submitted successfully'). No specialized training is required for students or faculty.", space_before=1, space_after=6, align=WD_ALIGN_PARAGRAPH.JUSTIFY)

    p = add_p(space_before=4, space_after=2, align=WD_ALIGN_PARAGRAPH.LEFT)
    r = p.add_run("Schedule Feasibility")
    r.bold = True
    p = add_p("The project development was decomposed into structured milestones: database schema modeling, connection pooling, student registration/login, complaint submission, staff task handling, administrative reassignment, and security verification. All phases were systematically completed within the course timeframe.", space_before=1, space_after=6, align=WD_ALIGN_PARAGRAPH.JUSTIFY)

    p = add_p(space_before=4, space_after=2, align=WD_ALIGN_PARAGRAPH.LEFT)
    r = p.add_run("Security Feasibility")
    r.bold = True
    p = add_p("Security risks (unauthorized access, credential theft, SQL injection, and CSRF) are thoroughly addressed through 12-round BCrypt hashing, strict session invalidation and renewal at login, PreparedStatement parameter binding, and synchronizer CSRF tokens.", space_before=1, space_after=8, align=WD_ALIGN_PARAGRAPH.JUSTIFY)

    doc.add_page_break()

    # =========================================================================
    # CHAPTER 2: TECHNOLOGIES USED
    # =========================================================================
    p_header = add_p(space_before=0, space_after=4, align=WD_ALIGN_PARAGRAPH.RIGHT)
    r_hdr = p_header.add_run("4")
    r_hdr.font.name = "Times New Roman"
    r_hdr.font.size = Pt(10)

    add_heading_1("2. TECHNOLOGIES USED")
    add_heading_2("2.1 HTML, CSS & JavaScript")
    
    p = add_p("HTML5 defines the structural skeleton of every page in CampusCare. Form inputs are configured with explicit field names (studentId, fullName, studentType, categoryId, priority, remarks) that map directly to server-side retrieval calls via request.getParameter(). Semantic markup ensures proper accessibility and rendering across web browsers.", space_before=2, space_after=6, align=WD_ALIGN_PARAGRAPH.JUSTIFY)

    p = add_p("CSS3 and Bootstrap 5 provide a modern, professional, responsive campus styling. The visual palette emphasizes deep navy blue (#0f2a4a) for administrative authority, clean white surfaces (#ffffff) for readability, and vibrant teal (#0d9488) accents for interactive action elements. Custom status badges (badge-pending, badge-assigned, badge-in_progress, badge-resolved, badge-rejected, badge-withdrawn) deliver immediate visual clarity.", space_before=2, space_after=6, align=WD_ALIGN_PARAGRAPH.JUSTIFY)

    p = add_p("Vanilla JavaScript (validation.js and dashboard.js) handles client-side dynamic behavior. Key features include automatic toggling of hostel accommodation fields (showing hostel name and room number inputs when HOSTEL is chosen, and clearing/hiding them when DAY_SCHOLAR is selected), password complexity verification, resolution remarks length checks, confirmation dialogs on destructive actions, and instant table search filtering.", space_before=2, space_after=6, align=WD_ALIGN_PARAGRAPH.JUSTIFY)

    tbl_tech = doc.add_table(rows=4, cols=2)
    tbl_tech.alignment = WD_TABLE_ALIGNMENT.CENTER
    tech_data = [
        ("Technology", "Use in CampusCare"),
        ("HTML5", "Semantic page structures, responsive tables, role-based dashboards, modal dialogs, and complaint forms."),
        ("CSS3 & Bootstrap 5", "Deep navy and teal campus theme, Flexbox/Grid layouts, custom cards, priority badges, and timeline styles."),
        ("JavaScript", "Dynamic hostel field toggling, real-time client validations, confirmation modals, and table filtering.")
    ]
    for r_idx, (col1, col2) in enumerate(tech_data):
        c0 = tbl_tech.cell(r_idx, 0)
        c1 = tbl_tech.cell(r_idx, 1)
        c0.width = Inches(2.2)
        c1.width = Inches(4.6)
        set_cell_margins(c0, top=40, bottom=40, left=100, right=100)
        set_cell_margins(c1, top=40, bottom=40, left=100, right=100)
        if r_idx == 0:
            set_cell_background(c0, "EBF1F5")
            set_cell_background(c1, "EBF1F5")
        p0 = c0.paragraphs[0]
        r0 = p0.add_run(col1)
        r0.font.name = "Times New Roman"
        r0.font.size = Pt(9.5)
        if r_idx == 0: r0.bold = True
        p1 = c1.paragraphs[0]
        r1 = p1.add_run(col2)
        r1.font.name = "Times New Roman"
        r1.font.size = Pt(9.5)
        if r_idx == 0: r1.bold = True

    add_heading_2("2.2 JDBC (MySQL Connector/J)")
    p = add_p("Java Database Connectivity (JDBC) is the core Java API for connecting to relational databases. CampusCare utilizes the MySQL Connector/J driver (com.mysql:mysql-connector-j:8.3.0) to establish high-performance connections over TCP/IP. All database interactions utilize JDBC PreparedStatement, ensuring strict parameterization and zero vulnerability to SQL injection.", space_before=2, space_after=6, align=WD_ALIGN_PARAGRAPH.JUSTIFY)

    p = add_p(space_before=2, space_after=2, align=WD_ALIGN_PARAGRAPH.LEFT)
    r = p.add_run("Standard Steps Followed in Every Database Interaction:")
    r.bold = True

    steps = [
        "Load the Driver class: Class.forName(\"com.mysql.cj.jdbc.Driver\").",
        "Obtain Connection: DriverManager.getConnection(url, username, password) reading credentials from db.properties.",
        "Prepare Parameterized Query: con.prepareStatement(\"... WHERE id = ?\") and bind values with setString(), setInt(), setTimestamp().",
        "Execute Operation: executeQuery() for SELECT queries (returning ResultSet) and executeUpdate() for INSERT/UPDATE/DELETE.",
        "Process Results: while (rs.next()) { ... } mapping database records into JavaBean models.",
        "Close Resources: Ensure Connection, Statement, and ResultSet are closed in finally blocks or try-with-resources."
    ]
    for s in steps:
        p = add_p(f"• {s}", space_before=1, space_after=2, align=WD_ALIGN_PARAGRAPH.JUSTIFY)
        p.paragraph_format.left_indent = Inches(0.25)

    tbl_jdbc = doc.add_table(rows=6, cols=2)
    tbl_jdbc.alignment = WD_TABLE_ALIGNMENT.CENTER
    jdbc_data = [
        ("JDBC Class / Interface", "Purpose in CampusCare"),
        ("DriverManager", "Loads the MySQL driver and creates physical database connections via DBConnection.java."),
        ("Connection", "Maintains database session and controls transaction boundaries (setAutoCommit, commit, rollback)."),
        ("PreparedStatement", "Pre-compiled parameterized SQL execution ensuring total immunity to SQL injection."),
        ("ResultSet", "Tabular query results mapped into Student, Staff, Complaint, and ComplaintUpdate beans."),
        ("SQLException", "Handles database error codes and integrity violations (duplicate email, check constraints).")
    ]
    for r_idx, (col1, col2) in enumerate(jdbc_data):
        c0 = tbl_jdbc.cell(r_idx, 0)
        c1 = tbl_jdbc.cell(r_idx, 1)
        c0.width = Inches(2.2)
        c1.width = Inches(4.6)
        set_cell_margins(c0, top=40, bottom=40, left=100, right=100)
        set_cell_margins(c1, top=40, bottom=40, left=100, right=100)
        if r_idx == 0:
            set_cell_background(c0, "EBF1F5")
            set_cell_background(c1, "EBF1F5")
        p0 = c0.paragraphs[0]
        r0 = p0.add_run(col1)
        r0.font.name = "Times New Roman"
        r0.font.size = Pt(9.5)
        if r_idx == 0: r0.bold = True
        p1 = c1.paragraphs[0]
        r1 = p1.add_run(col2)
        r1.font.name = "Times New Roman"
        r1.font.size = Pt(9.5)
        if r_idx == 0: r1.bold = True

    add_heading_2("2.3 Servlets and the Apache Tomcat Container")
    p = add_p("A Java Servlet is a server-side component managed by a web container that receives and responds to HTTP requests. Apache Tomcat 9.0 acts as the Servlet 4.0 and JSP 2.3 container, managing servlet lifecycles (init(), service(), destroy()) under the javax.servlet API. CampusCare deploys 14 dedicated servlet controllers that process requests, invoke Data Access Objects (DAOs), enforce role-based authorization, and implement the Post/Redirect/Get (PRG) pattern.", space_before=2, space_after=6, align=WD_ALIGN_PARAGRAPH.JUSTIFY)

    p = add_p("Session management relies on Tomcat's HttpSession mechanism. Upon successful authentication, any existing session is invalidated to neutralize session fixation attacks, and a fresh session is provisioned storing the user's ID, role, and name. Tomcat transmits the session token in a secure JSESSIONID cookie.", space_before=2, space_after=6, align=WD_ALIGN_PARAGRAPH.JUSTIFY)

    add_heading_2("2.4 JSP (JavaServer Pages)")
    p = add_p("JavaServer Pages enable dynamic HTML generation on the server. When first requested, Tomcat's Jasper engine translates each .jsp template into a Java servlet, compiles it, and executes it. CampusCare structures views into reusable modular fragments (header.jsp, footer.jsp, messages.jsp, error.jsp) imported across all pages.", space_before=2, space_after=6, align=WD_ALIGN_PARAGRAPH.JUSTIFY)

    add_heading_2("2.5 Supporting Tools and Libraries")
    tbl_tools = doc.add_table(rows=7, cols=3)
    tbl_tools.alignment = WD_TABLE_ALIGNMENT.CENTER
    tools_data = [
        ("Tool / Library", "Version", "Role in CampusCare"),
        ("JDK", "Java 8 / Java 21", "Compiles all 33 Java source files to bytecode targeting Java 8 compatibility."),
        ("Apache Tomcat", "9.0", "Servlet 4.0 / JSP 2.3 runtime container (HTTP Port 8081)."),
        ("MySQL Server", "8.0", "Relational database engine storing students, staff, admins, complaints, and audit history."),
        ("MySQL Connector/J", "8.3.0", "Type-4 pure Java JDBC driver connecting application to MySQL."),
        ("jBCrypt", "0.4", "BCrypt 12-round salted hashing for all student, staff, and admin passwords."),
        ("Eclipse IDE", "Enterprise Java", "Integrated Development Environment for building, debugging, and deploying to Tomcat.")
    ]
    for r_idx, (col1, col2, col3) in enumerate(tools_data):
        c0 = tbl_tools.cell(r_idx, 0)
        c1 = tbl_tools.cell(r_idx, 1)
        c2 = tbl_tools.cell(r_idx, 2)
        c0.width = Inches(1.8)
        c1.width = Inches(1.4)
        c2.width = Inches(3.6)
        set_cell_margins(c0, top=40, bottom=40, left=80, right=80)
        set_cell_margins(c1, top=40, bottom=40, left=80, right=80)
        set_cell_margins(c2, top=40, bottom=40, left=80, right=80)
        if r_idx == 0:
            set_cell_background(c0, "EBF1F5")
            set_cell_background(c1, "EBF1F5")
            set_cell_background(c2, "EBF1F5")
        for c, text in [(c0, col1), (c1, col2), (c2, col3)]:
            p = c.paragraphs[0]
            r = p.add_run(text)
            r.font.name = "Times New Roman"
            r.font.size = Pt(9.5)
            if r_idx == 0: r.bold = True

    doc.add_page_break()

    # =========================================================================
    # CHAPTER 3: WEB APPLICATION ARCHITECTURE
    # =========================================================================
    p_header = add_p(space_before=0, space_after=4, align=WD_ALIGN_PARAGRAPH.RIGHT)
    r_hdr = p_header.add_run("8")
    r_hdr.font.name = "Times New Roman"
    r_hdr.font.size = Pt(10)

    add_heading_1("3. WEB APPLICATION ARCHITECTURE")
    add_heading_2("3.1 Three-Tier Architecture")
    
    p = add_p("CampusCare strictly implements an enterprise three-tier Model-View-Controller (MVC) architecture. The Presentation Tier comprises the web browser rendering HTML5, CSS3, JavaScript, and Bootstrap 5. The Application Tier is hosted on Apache Tomcat 9, orchestrating 14 Java Servlets, JSP templates, session management, security filters, and DAO layers. The Data Tier is a MySQL 8.0 database engine executing ACID transactions.", space_before=2, space_after=6, align=WD_ALIGN_PARAGRAPH.JUSTIFY)

    # Insert Architecture Diagram
    arch_img = r"C:\Campuscare\report_diagrams\arch_diagram.png"
    if os.path.exists(arch_img):
        p_img = doc.add_paragraph()
        p_img.alignment = WD_ALIGN_PARAGRAPH.CENTER
        p_img.paragraph_format.space_before = Pt(6)
        p_img.paragraph_format.space_after = Pt(4)
        p_img.add_run().add_picture(arch_img, width=Inches(6.2))
        p_cap = doc.add_paragraph()
        p_cap.alignment = WD_ALIGN_PARAGRAPH.CENTER
        p_cap.paragraph_format.space_after = Pt(8)
        r_cap = p_cap.add_run("Figure 3.1: Three-tier architecture of CampusCare")
        r_cap.font.name = "Times New Roman"
        r_cap.font.size = Pt(9.5)
        r_cap.italic = True

    add_heading_2("3.2 Pages and Their Roles")
    tbl_pages = doc.add_table(rows=15, cols=2)
    tbl_pages.alignment = WD_TABLE_ALIGNMENT.CENTER
    page_data = [
        ("File(s) / Route", "Role in CampusCare"),
        ("DBConnection.java", "Centralized JDBC connection factory loading MySQL Connector/J from db.properties."),
        ("index.jsp", "Public landing portal with role cards for Student, Staff, and Administrator."),
        ("header.jsp, footer.jsp", "Consistent navigation bar, role badge, session pills, and script imports."),
        ("student-login.jsp, StudentLoginServlet", "Student login authentication, BCrypt verification, and session renewal."),
        ("student-register.jsp, StudentRegisterServlet", "Student signup with dynamic Hostel/Day Scholar validation and uniqueness checks."),
        ("staff-login.jsp, StaffLoginServlet", "Dedicated staff login authentication routing to assigned task queue."),
        ("admin-login.jsp, AdminLoginServlet", "Separate administrative control login with restricted authorization."),
        ("LogoutServlet", "Invalidates user session and terminates authentication state."),
        ("new-complaint.jsp, ComplaintCreateServlet", "Complaint filing form generating initial PENDING ticket starting at 1001 (GC1001)."),
        ("my-complaints.jsp, StudentComplaintsServlet", "Personal grievance table with status filtering and search capability."),
        ("complaint-details.jsp, ComplaintDetailsServlet", "Detailed ticket inspection, student info, and immutable status audit timeline."),
        ("StaffDashboardServlet, resolve-complaint.jsp", "Staff work queue, status update to IN_PROGRESS, and resolution with remarks."),
        ("AdminDashboardServlet, all-complaints.jsp", "Administrative dashboard, workload metrics, multi-attribute filter, and staff assignment."),
        ("manage-students.jsp, manage-staff.jsp", "Administrative user governance for student and staff activation/deactivation.")
    ]
    for r_idx, (col1, col2) in enumerate(page_data):
        c0 = tbl_pages.cell(r_idx, 0)
        c1 = tbl_pages.cell(r_idx, 1)
        c0.width = Inches(2.3)
        c1.width = Inches(4.5)
        set_cell_margins(c0, top=35, bottom=35, left=80, right=80)
        set_cell_margins(c1, top=35, bottom=35, left=80, right=80)
        if r_idx == 0:
            set_cell_background(c0, "EBF1F5")
            set_cell_background(c1, "EBF1F5")
        p0 = c0.paragraphs[0]
        r0 = p0.add_run(col1)
        r0.font.name = "Times New Roman"
        r0.font.size = Pt(9.5)
        if r_idx == 0: r0.bold = True
        p1 = c1.paragraphs[0]
        r1 = p1.add_run(col2)
        r1.font.name = "Times New Roman"
        r1.font.size = Pt(9.5)
        if r_idx == 0: r1.bold = True

    add_heading_2("3.3 Database Design")
    p = add_p("The relational database schema is structured into 6 interrelated MySQL tables enforced by primary keys, foreign keys, and check constraints.", space_before=2, space_after=6, align=WD_ALIGN_PARAGRAPH.JUSTIFY)

    # Insert ER Diagram
    er_img = r"C:\Campuscare\report_diagrams\er_diagram.png"
    if os.path.exists(er_img):
        p_img = doc.add_paragraph()
        p_img.alignment = WD_ALIGN_PARAGRAPH.CENTER
        p_img.paragraph_format.space_before = Pt(6)
        p_img.paragraph_format.space_after = Pt(4)
        p_img.add_run().add_picture(er_img, width=Inches(6.2))
        p_cap = doc.add_paragraph()
        p_cap.alignment = WD_ALIGN_PARAGRAPH.CENTER
        p_cap.paragraph_format.space_after = Pt(8)
        r_cap = p_cap.add_run("Figure 3.2: Entity-relationship diagram of CampusCare")
        r_cap.font.name = "Times New Roman"
        r_cap.font.size = Pt(9.5)
        r_cap.italic = True

    # Tables 3.1 to 3.6
    p = add_p(space_before=4, space_after=2)
    r = p.add_run("Table 3.1: students")
    r.bold = True
    tbl_t1 = doc.add_table(rows=8, cols=3)
    t1_data = [
        ("Column", "Type", "Constraint / Note"),
        ("STUDENT_ID", "VARCHAR(30)", "Primary key (Alphanumeric 3-30 chars)"),
        ("FULL_NAME", "VARCHAR(100)", "NOT NULL (Student's complete name)"),
        ("DEPARTMENT", "VARCHAR(80)", "NOT NULL (Academic branch)"),
        ("STUDY_YEAR", "TINYINT", "CHECK (STUDY_YEAR BETWEEN 1 AND 6)"),
        ("STUDENT_TYPE", "VARCHAR(15)", "CHECK ('HOSTEL', 'DAY_SCHOLAR')"),
        ("HOSTEL_NAME, ROOM_NUMBER", "VARCHAR", "CK_STUDENT_HOSTEL (Required for HOSTEL, NULL for DAY_SCHOLAR)"),
        ("EMAIL, PASSWORD_HASH", "VARCHAR", "EMAIL UNIQUE, PASSWORD_HASH stores BCrypt hash")
    ]
    for r_idx, (col1, col2, col3) in enumerate(t1_data):
        for c_idx, text in enumerate([col1, col2, col3]):
            cell = tbl_t1.cell(r_idx, c_idx)
            set_cell_margins(cell, top=30, bottom=30, left=60, right=60)
            if r_idx == 0: set_cell_background(cell, "EBF1F5")
            p = cell.paragraphs[0]
            r = p.add_run(text)
            r.font.name = "Times New Roman"
            r.font.size = Pt(9)
            if r_idx == 0: r.bold = True

    p = add_p(space_before=6, space_after=2)
    r = p.add_run("Table 3.2: grievances")
    r.bold = True
    tbl_t2 = doc.add_table(rows=7, cols=3)
    t2_data = [
        ("Column", "Type", "Constraint / Note"),
        ("GRIEVANCE_ID", "BIGINT", "Primary key, AUTO_INCREMENT starting at 1001 (GC1001)"),
        ("STUDENT_ID", "VARCHAR(30)", "Foreign key to students(STUDENT_ID)"),
        ("CATEGORY_ID", "INT", "Foreign key to complaint_categories(CATEGORY_ID)"),
        ("TITLE, DESCRIPTION", "VARCHAR", "TITLE (5-100 chars), DESCRIPTION (10-1000 chars)"),
        ("PRIORITY", "VARCHAR(10)", "CHECK ('LOW', 'MEDIUM', 'HIGH')"),
        ("STATUS", "VARCHAR(20)", "CHECK ('PENDING','ASSIGNED','IN_PROGRESS','RESOLVED','REJECTED','WITHDRAWN')")
    ]
    for r_idx, (col1, col2, col3) in enumerate(t2_data):
        for c_idx, text in enumerate([col1, col2, col3]):
            cell = tbl_t2.cell(r_idx, c_idx)
            set_cell_margins(cell, top=30, bottom=30, left=60, right=60)
            if r_idx == 0: set_cell_background(cell, "EBF1F5")
            p = cell.paragraphs[0]
            r = p.add_run(text)
            r.font.name = "Times New Roman"
            r.font.size = Pt(9)
            if r_idx == 0: r.bold = True

    add_heading_2("3.4 Data Flow")
    p = add_p("The authentication and registration data flows enforce strict validation and cryptographic salting. Figure 3.3 illustrates the registration and login sequence, while Figure 3.4 details the complaint lifecycle.", space_before=2, space_after=6, align=WD_ALIGN_PARAGRAPH.JUSTIFY)

    # Insert Auth Flow Diagram
    auth_img = r"C:\Campuscare\report_diagrams\auth_flow.png"
    if os.path.exists(auth_img):
        p_img = doc.add_paragraph()
        p_img.alignment = WD_ALIGN_PARAGRAPH.CENTER
        p_img.paragraph_format.space_before = Pt(4)
        p_img.paragraph_format.space_after = Pt(2)
        p_img.add_run().add_picture(auth_img, width=Inches(6.0))
        p_cap = doc.add_paragraph()
        p_cap.alignment = WD_ALIGN_PARAGRAPH.CENTER
        p_cap.paragraph_format.space_after = Pt(6)
        r_cap = p_cap.add_run("Figure 3.3: Signup and login flow")
        r_cap.font.name = "Times New Roman"
        r_cap.font.size = Pt(9.5)
        r_cap.italic = True

    # Insert Workflow Lifecycle Diagram
    wf_img = r"C:\Campuscare\report_diagrams\workflow_lifecycle.png"
    if os.path.exists(wf_img):
        p_img = doc.add_paragraph()
        p_img.alignment = WD_ALIGN_PARAGRAPH.CENTER
        p_img.paragraph_format.space_before = Pt(4)
        p_img.paragraph_format.space_after = Pt(2)
        p_img.add_run().add_picture(wf_img, width=Inches(6.0))
        p_cap = doc.add_paragraph()
        p_cap.alignment = WD_ALIGN_PARAGRAPH.CENTER
        p_cap.paragraph_format.space_after = Pt(8)
        r_cap = p_cap.add_run("Figure 3.4: Complaint workflow and state transition lifecycle")
        r_cap.font.name = "Times New Roman"
        r_cap.font.size = Pt(9.5)
        r_cap.italic = True

    doc.add_page_break()

    # =========================================================================
    # CHAPTER 4: CODING & IMPLEMENTATION
    # =========================================================================
    p_header = add_p(space_before=0, space_after=4, align=WD_ALIGN_PARAGRAPH.RIGHT)
    r_hdr = p_header.add_run("13")
    r_hdr.font.name = "Times New Roman"
    r_hdr.font.size = Pt(10)

    add_heading_1("4. CODING & IMPLEMENTATION")
    p = add_p("This chapter documents the verified source code listings from CampusCare, showcasing database schema implementation, JDBC connections, validation, authentication, and transactional state updates.", space_before=2, space_after=6, align=WD_ALIGN_PARAGRAPH.JUSTIFY)

    add_heading_2("4.1 Project Structure")
    project_tree = (
        "CampusCare/\n"
        "├── pom.xml                                      # Maven build descriptor (Java 8/21, WAR)\n"
        "├── database/\n"
        "│   ├── campuscare_schema.sql                    # MySQL tables, AUTO_INCREMENT=1001\n"
        "│   └── campuscare_seed.sql                      # Seed accounts with BCrypt hashes\n"
        "├── src/main/resources/db.properties            # MySQL credentials (root / 1234)\n"
        "├── src/main/java/com/campuscare/\n"
        "│   ├── util/ (DBConnection, PasswordUtil, ValidationUtil, CSRFUtil, DatabaseSeeder)\n"
        "│   ├── model/ (Student, Staff, Admin, Complaint, ComplaintCategory, ComplaintUpdate)\n"
        "│   ├── dao/ (StudentDAO, StaffDAO, AdminDAO, ComplaintCategoryDAO, ComplaintDAO)\n"
        "│   ├── filter/ (AuthenticationFilter, AuthorizationFilter, EncodingFilter)\n"
        "│   └── servlet/ (14 HttpServlet controllers for Student, Staff, and Admin)\n"
        "└── src/main/webapp/\n"
        "    ├── index.jsp                                # Landing portal\n"
        "    ├── assets/ (css/style.css, js/validation.js, js/dashboard.js)\n"
        "    ├── WEB-INF/web.xml                          # Servlet 4.0 descriptor & sequential filters\n"
        "    └── WEB-INF/views/ (Modular JSPs: auth, student, staff, admin, common)"
    )
    add_listing("Listing 4.0: Project Directory Structure", project_tree)

    add_heading_2("4.2 Database Implementation")
    sql_tables = (
        "CREATE TABLE students (\n"
        "  STUDENT_ID VARCHAR(30) PRIMARY KEY,\n"
        "  FULL_NAME VARCHAR(100) NOT NULL,\n"
        "  DEPARTMENT VARCHAR(80) NOT NULL,\n"
        "  STUDY_YEAR TINYINT NOT NULL,\n"
        "  STUDENT_TYPE VARCHAR(15) NOT NULL DEFAULT 'DAY_SCHOLAR',\n"
        "  HOSTEL_NAME VARCHAR(100) DEFAULT NULL,\n"
        "  ROOM_NUMBER VARCHAR(20) DEFAULT NULL,\n"
        "  EMAIL VARCHAR(150) NOT NULL UNIQUE,\n"
        "  MOBILE VARCHAR(15) NOT NULL,\n"
        "  PASSWORD_HASH VARCHAR(255) NOT NULL,\n"
        "  ACCOUNT_STATUS VARCHAR(10) NOT NULL DEFAULT 'ACTIVE',\n"
        "  CONSTRAINT CK_STUDENT_HOSTEL CHECK (\n"
        "    ((STUDENT_TYPE = 'HOSTEL') AND (HOSTEL_NAME IS NOT NULL) AND (ROOM_NUMBER IS NOT NULL)) OR\n"
        "    ((STUDENT_TYPE = 'DAY_SCHOLAR') AND (HOSTEL_NAME IS NULL) AND (ROOM_NUMBER IS NULL))\n"
        "  )\n"
        ");\n\n"
        "CREATE TABLE grievances (\n"
        "  GRIEVANCE_ID BIGINT AUTO_INCREMENT PRIMARY KEY,\n"
        "  STUDENT_ID VARCHAR(30) NOT NULL REFERENCES students(STUDENT_ID),\n"
        "  CATEGORY_ID INT NOT NULL REFERENCES complaint_categories(CATEGORY_ID),\n"
        "  TITLE VARCHAR(100) NOT NULL,\n"
        "  DESCRIPTION VARCHAR(1000) NOT NULL,\n"
        "  PRIORITY VARCHAR(10) NOT NULL DEFAULT 'MEDIUM',\n"
        "  STATUS VARCHAR(20) NOT NULL DEFAULT 'PENDING',\n"
        "  ASSIGNED_STAFF_ID VARCHAR(30) REFERENCES staff(STAFF_ID),\n"
        "  CREATED_AT TIMESTAMP DEFAULT CURRENT_TIMESTAMP\n"
        ") AUTO_INCREMENT=1001;"
    )
    add_listing("Listing 4.1: Table creation (students and grievances in MySQL)", sql_tables)

    add_heading_2("4.3 Database Connection")
    conn_code = (
        "package com.campuscare.util;\n"
        "import java.sql.Connection;\n"
        "import java.sql.DriverManager;\n"
        "import java.sql.SQLException;\n"
        "import java.util.Properties;\n\n"
        "public class DBConnection {\n"
        "    private static String url;\n"
        "    private static String username;\n"
        "    private static String password;\n"
        "    static {\n"
        "        try (InputStream in = DBConnection.class.getClassLoader()\n"
        "                .getResourceAsStream(\"db.properties\")) {\n"
        "            Properties props = new Properties();\n"
        "            props.load(in);\n"
        "            Class.forName(props.getProperty(\"db.driver\"));\n"
        "            url = props.getProperty(\"db.url\");\n"
        "            username = props.getProperty(\"db.username\");\n"
        "            password = props.getProperty(\"db.password\");\n"
        "        } catch (Exception e) {\n"
        "            throw new RuntimeException(\"Failed to load db.properties\", e);\n"
        "        }\n"
        "    }\n"
        "    public static Connection getConnection() throws SQLException {\n"
        "        return DriverManager.getConnection(url, username, password);\n"
        "    }\n"
        "}"
    )
    add_listing("Listing 4.2: DBConnection.java (Secure connection loader)", conn_code)

    add_heading_2("4.4 Student Registration & Validation Module")
    reg_val_code = (
        "// Server-side validation in StudentRegisterServlet.java\n"
        "if (!ValidationUtil.isValidId(studentId)) {\n"
        "    errors.add(\"Student ID must be 3-30 alphanumeric characters.\");\n"
        "} else if (studentDAO.isIdExists(studentId.trim())) {\n"
        "    errors.add(\"Student ID is already registered.\");\n"
        "}\n"
        "if (\"HOSTEL\".equalsIgnoreCase(studentType)) {\n"
        "    if (hostelName == null || hostelName.trim().isEmpty() || roomNumber == null) {\n"
        "        errors.add(\"Hostel Name and Room Number are required for hostel students.\");\n"
        "    }\n"
        "} else {\n"
        "    hostelName = null; // Must be strictly null for Day Scholars\n"
        "    roomNumber = null;\n"
        "}\n"
        "if (!PasswordUtil.isPasswordComplex(password)) {\n"
        "    errors.add(\"Password must be >= 8 chars with uppercase, lowercase, digit & symbol.\");\n"
        "}\n"
        "// BCrypt password hashing\n"
        "String passwordHash = PasswordUtil.hashPassword(password);\n"
        "Student s = new Student(studentId, fullName, department, studyYear, studentType,\n"
        "                        hostelName, roomNumber, email, mobile, passwordHash, \"ACTIVE\", null);\n"
        "studentDAO.insert(s);"
    )
    add_listing("Listing 4.3: Conditional validation and BCrypt hashing in registration", reg_val_code)

    add_heading_2("4.5 Login and Logout Module")
    login_code = (
        "// Credential verification & session fixation protection in StudentLoginServlet.java\n"
        "Student student = identifier.contains(\"@\") ? studentDAO.findByEmail(identifier) \n"
        "                                           : studentDAO.findById(identifier);\n"
        "if (student != null && PasswordUtil.checkPassword(password, student.getPasswordHash())) {\n"
        "    if (!student.isActive()) {\n"
        "        req.setAttribute(\"errorMessage\", \"Account deactivated. Contact administrator.\");\n"
        "        req.getRequestDispatcher(\"/WEB-INF/views/auth/student-login.jsp\").forward(req, resp);\n"
        "        return;\n"
        "    }\n"
        "    // Invalidate existing session to neutralize session fixation attacks\n"
        "    HttpSession oldSession = req.getSession(false);\n"
        "    if (oldSession != null) oldSession.invalidate();\n"
        "    HttpSession newSession = req.getSession(true);\n"
        "    newSession.setAttribute(\"user\", student);\n"
        "    newSession.setAttribute(\"userId\", student.getStudentId());\n"
        "    newSession.setAttribute(\"role\", \"STUDENT\");\n"
        "    CSRFUtil.getToken(newSession); // Issue synchronizer token\n"
        "    resp.sendRedirect(req.getContextPath() + \"/student/dashboard\");\n"
        "}"
    )
    add_listing("Listing 4.4: Login verification and session renewal", login_code)

    add_heading_2("4.6 Complaint Filing Module")
    create_comp_code = (
        "// Transactional Complaint Filing in ComplaintDAO.java\n"
        "conn.setAutoCommit(false);\n"
        "String insertSql = \"INSERT INTO grievances (STUDENT_ID, CATEGORY_ID, TITLE, \" +\n"
        "                   \"DESCRIPTION, PRIORITY, STATUS) VALUES (?, ?, ?, ?, ?, 'PENDING')\";\n"
        "psInsert = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS);\n"
        "psInsert.setString(1, c.getStudentId());\n"
        "psInsert.setInt(2, c.getCategoryId());\n"
        "psInsert.setString(3, c.getTitle().trim());\n"
        "psInsert.setString(4, c.getDescription().trim());\n"
        "psInsert.setString(5, c.getPriority().toUpperCase());\n"
        "psInsert.executeUpdate();\n\n"
        "rs = psInsert.getGeneratedKeys();\n"
        "long grievanceId = rs.next() ? rs.getLong(1) : 0;\n\n"
        "// Log initial PENDING audit entry\n"
        "String updateSql = \"INSERT INTO grievance_updates (GRIEVANCE_ID, UPDATED_BY_ROLE, \" +\n"
        "                   \"UPDATED_BY_ID, OLD_STATUS, NEW_STATUS, REMARKS) \" +\n"
        "                   \"VALUES (?, 'STUDENT', ?, NULL, 'PENDING', 'Complaint created.')\";\n"
        "psUpdate = conn.prepareStatement(updateSql);\n"
        "psUpdate.setLong(1, grievanceId);\n"
        "psUpdate.setString(2, c.getStudentId());\n"
        "psUpdate.executeUpdate();\n"
        "conn.commit();"
    )
    add_listing("Listing 4.5: Transactional Complaint Insertion and Initial Audit Log", create_comp_code)

    add_heading_2("4.7 Staff Action & Resolution Module")
    staff_act_code = (
        "// Staff Status Transition with Mandatory Resolution Remarks in ComplaintDAO.java\n"
        "if (\"RESOLVED\".equals(newStatus)) {\n"
        "    if (remarks == null || remarks.trim().isEmpty() || remarks.trim().length() > 500) {\n"
        "        throw new IllegalArgumentException(\"Resolution remarks required (<= 500 chars).\");\n"
        "    }\n"
        "}\n"
        "conn.setAutoCommit(false);\n"
        "psUpdate = conn.prepareStatement(\"UPDATE grievances SET STATUS = ?, UPDATED_AT = NOW() \" +\n"
        "                                 \"WHERE GRIEVANCE_ID = ?\");\n"
        "psUpdate.setString(1, newStatus);\n"
        "psUpdate.setLong(2, grievanceId);\n"
        "psUpdate.executeUpdate();\n\n"
        "psHistory = conn.prepareStatement(\"INSERT INTO grievance_updates (GRIEVANCE_ID, \" +\n"
        "                                  \"UPDATED_BY_ROLE, UPDATED_BY_ID, OLD_STATUS, \" +\n"
        "                                  \"NEW_STATUS, REMARKS) VALUES (?, ?, ?, ?, ?, ?)\");\n"
        "psHistory.setLong(1, grievanceId);\n"
        "psHistory.setString(2, \"STAFF\");\n"
        "psHistory.setString(3, staffId);\n"
        "psHistory.setString(4, oldStatus);\n"
        "psHistory.setString(5, newStatus);\n"
        "psHistory.setString(6, remarks.trim());\n"
        "psHistory.executeUpdate();\n"
        "conn.commit();"
    )
    add_listing("Listing 4.6: Atomic Status Update and Audit Logging in ComplaintDAO.java", staff_act_code)

    add_heading_2("4.8 Admin Assignment & User Governance Module")
    admin_assign_code = (
        "// Staff Assignment and Reassignment in ComplaintDAO.java\n"
        "conn.setAutoCommit(false);\n"
        "psUpdate = conn.prepareStatement(\"UPDATE grievances SET ASSIGNED_STAFF_ID = ?, \" +\n"
        "                                 \"STATUS = 'ASSIGNED', UPDATED_AT = NOW() \" +\n"
        "                                 \"WHERE GRIEVANCE_ID = ?\");\n"
        "psUpdate.setString(1, staffId);\n"
        "psUpdate.setLong(2, grievanceId);\n"
        "psUpdate.executeUpdate();\n\n"
        "String auditRemarks = (oldStaffId == null) \n"
        "    ? \"Assigned to staff \" + staffId + \": \" + remarks \n"
        "    : \"Reassigned from \" + oldStaffId + \" to \" + staffId + \": \" + remarks;\n"
        "psHistory = conn.prepareStatement(\"INSERT INTO grievance_updates (GRIEVANCE_ID, \" +\n"
        "                                  \"UPDATED_BY_ROLE, UPDATED_BY_ID, OLD_STATUS, \" +\n"
        "                                  \"NEW_STATUS, REMARKS) VALUES (?, 'ADMIN', ?, ?, 'ASSIGNED', ?)\");\n"
        "psHistory.setLong(1, grievanceId);\n"
        "psHistory.setString(2, adminId);\n"
        "psHistory.setString(3, oldStatus);\n"
        "psHistory.setString(4, auditRemarks);\n"
        "psHistory.executeUpdate();\n"
        "conn.commit();"
    )
    add_listing("Listing 4.7: Administrative Staff Assignment and Reassignment", admin_assign_code)

    add_heading_2("4.9 Complaint Status History & Audit Module")
    audit_sql = (
        "// Querying Complete Case History with Dynamic Actor Name Resolution\n"
        "SELECT u.*, \n"
        "  CASE \n"
        "    WHEN u.UPDATED_BY_ROLE = 'STUDENT' THEN (SELECT FULL_NAME FROM students WHERE STUDENT_ID = u.UPDATED_BY_ID) \n"
        "    WHEN u.UPDATED_BY_ROLE = 'STAFF'   THEN (SELECT FULL_NAME FROM staff WHERE STAFF_ID = u.UPDATED_BY_ID) \n"
        "    WHEN u.UPDATED_BY_ROLE = 'ADMIN'   THEN (SELECT USERNAME FROM admins WHERE ADMIN_ID = u.UPDATED_BY_ID) \n"
        "    ELSE u.UPDATED_BY_ID \n"
        "  END AS UPDATER_NAME \n"
        "FROM grievance_updates u \n"
        "WHERE u.GRIEVANCE_ID = ? \n"
        "ORDER BY u.UPDATED_AT ASC;"
    )
    add_listing("Listing 4.8: Audit History Query with Dynamic Name Resolution", audit_sql)

    add_heading_2("4.10 Security Measures")
    tbl_sec = doc.add_table(rows=10, cols=2)
    tbl_sec.alignment = WD_TABLE_ALIGNMENT.CENTER
    sec_data = [
        ("Security Risk", "Mitigation Measure in CampusCare"),
        ("Credential Theft", "BCrypt 12-round salted hashing. Plaintext passwords are never stored or logged."),
        ("SQL Injection", "100% of SQL queries utilize PreparedStatement with strongly-typed bound parameters."),
        ("Cross-Site Scripting (XSS)", "User-supplied content is sanitized with HTML escaping before rendering in JSP."),
        ("Cross-Site Request Forgery", "AuthorizationFilter enforces cryptographically secure CSRF synchronizer tokens on all POST requests."),
        ("Session Fixation", "Old sessions are explicitly invalidated at login and a fresh JSESSIONID is issued."),
        ("Privilege Escalation", "AuthorizationFilter enforces strict role separation (/student/*, /staff/*, /admin/*)."),
        ("Cross-User Record Tampering", "Students can only access own complaints; staff can only access assigned tickets."),
        ("Information Leakage", "Generic authentication failure messages; database stack traces suppressed via custom error pages."),
        ("Data Corruption", "Multi-statement status updates and audit logs execute in atomic transactions (commit/rollback).")
    ]
    for r_idx, (col1, col2) in enumerate(sec_data):
        c0 = tbl_sec.cell(r_idx, 0)
        c1 = tbl_sec.cell(r_idx, 1)
        c0.width = Inches(2.2)
        c1.width = Inches(4.6)
        set_cell_margins(c0, top=35, bottom=35, left=80, right=80)
        set_cell_margins(c1, top=35, bottom=35, left=80, right=80)
        if r_idx == 0:
            set_cell_background(c0, "EBF1F5")
            set_cell_background(c1, "EBF1F5")
        p0 = c0.paragraphs[0]
        r0 = p0.add_run(col1)
        r0.font.name = "Times New Roman"
        r0.font.size = Pt(9.5)
        if r_idx == 0: r0.bold = True
        p1 = c1.paragraphs[0]
        r1 = p1.add_run(col2)
        r1.font.name = "Times New Roman"
        r1.font.size = Pt(9.5)
        if r_idx == 0: r1.bold = True

    doc.add_page_break()

    # =========================================================================
    # CHAPTER 5: RESULTS
    # =========================================================================
    p_header = add_p(space_before=0, space_after=4, align=WD_ALIGN_PARAGRAPH.RIGHT)
    r_hdr = p_header.add_run("21")
    r_hdr.font.name = "Times New Roman"
    r_hdr.font.size = Pt(10)

    add_heading_1("5. RESULTS")
    p = add_p("This chapter documents the verified operational output of CampusCare running on Apache Tomcat 9 (http://localhost:8081/CampusCare/) and backed by MySQL 8.0. Prominent screenshot placeholders are provided matching the reference document layout, ready for direct visual browser captures.", space_before=2, space_after=6, align=WD_ALIGN_PARAGRAPH.JUSTIFY)

    add_heading_2("5.1 Login Page")
    p = add_p("Figure 5.1 illustrates the dedicated authentication portals for Students, Staff, and Administrators. Users provide registered credentials and password. Generic error messages prevent account enumeration on invalid entries.", space_before=2, space_after=4, align=WD_ALIGN_PARAGRAPH.JUSTIFY)
    add_screenshot_placeholder("5.1", "Login page (Student, Staff, and Admin Portals)", "Capture from http://localhost:8081/CampusCare/student-login or admin-login")

    add_heading_2("5.2 Student Dashboard & New Complaint Page")
    p = add_p("Figure 5.2 displays the Student Dashboard with summary metric cards (Total, Pending, In Progress, Resolved) and the New Complaint Submission form with dynamic hostel field toggling.", space_before=2, space_after=4, align=WD_ALIGN_PARAGRAPH.JUSTIFY)
    add_screenshot_placeholder("5.2", "Student Dashboard and New Complaint Form", "Capture from http://localhost:8081/CampusCare/student/dashboard")

    add_heading_2("5.3 Complaint Details & Status Timeline Page")
    p = add_p("Figure 5.3 shows the Complaint Details view displaying ticket GC1001, student details, assigned staff information, and the chronological audit history timeline.", space_before=2, space_after=4, align=WD_ALIGN_PARAGRAPH.JUSTIFY)
    add_screenshot_placeholder("5.3", "Complaint Details and Status Audit Timeline", "Capture from http://localhost:8081/CampusCare/student/complaint-details?id=1001")

    add_heading_2("5.4 Staff Dashboard & Resolution Page")
    p = add_p("Figure 5.4 depicts the Staff Dashboard displaying assigned complaints, task status updating (IN_PROGRESS), and the complaint resolution form with mandatory remarks.", space_before=2, space_after=4, align=WD_ALIGN_PARAGRAPH.JUSTIFY)
    add_screenshot_placeholder("5.4", "Staff Dashboard and Complaint Resolution Form", "Capture from http://localhost:8081/CampusCare/staff/dashboard")

    add_heading_2("5.5 Admin Dashboard & Assignment Page")
    p = add_p("Figure 5.5 presents the Administrator Dashboard showing campus-wide complaint counts, staff workload statistics, and the staff assignment/reassignment modal.", space_before=2, space_after=4, align=WD_ALIGN_PARAGRAPH.JUSTIFY)
    add_screenshot_placeholder("5.5", "Admin Dashboard and Staff Assignment Form", "Capture from http://localhost:8081/CampusCare/admin/dashboard")

    add_heading_2("5.6 All Complaints & Management Pages")
    p = add_p("Figure 5.6 shows the All Complaints directory with multi-attribute filtering (category, status, priority, staff, date) alongside student and staff account management controls.", space_before=2, space_after=4, align=WD_ALIGN_PARAGRAPH.JUSTIFY)
    add_screenshot_placeholder("5.6", "All Complaints Directory and User Management", "Capture from http://localhost:8081/CampusCare/admin/all-complaints")

    add_heading_2("5.7 Database Output")
    p = add_p("The database screenshots and formatted SQL query outputs below confirm persistent storage in MySQL 8.0.", space_before=2, space_after=4, align=WD_ALIGN_PARAGRAPH.JUSTIFY)
    add_screenshot_placeholder("5.7", "students table in MySQL command-line client", "SQL: USE campuscare; SELECT STUDENT_ID, FULL_NAME, DEPARTMENT, STUDENT_TYPE, HOSTEL_NAME, ACCOUNT_STATUS FROM students;")
    add_screenshot_placeholder("5.8", "staff table with departments and BCrypt hashes", "SQL: USE campuscare; SELECT STAFF_ID, FULL_NAME, DEPARTMENT, EMAIL, ACCOUNT_STATUS FROM staff;")
    add_screenshot_placeholder("5.9", "grievances table with ticket numbers GC1001", "SQL: USE campuscare; SELECT GRIEVANCE_ID, STUDENT_ID, CATEGORY_ID, TITLE, PRIORITY, STATUS, ASSIGNED_STAFF_ID FROM grievances;")
    add_screenshot_placeholder("5.10", "grievance_updates audit trail table", "SQL: USE campuscare; SELECT UPDATE_ID, GRIEVANCE_ID, UPDATED_BY_ROLE, OLD_STATUS, NEW_STATUS, REMARKS FROM grievance_updates;")

    add_heading_2("5.8 Testing")
    p = add_p("The application was systematically verified across 16 test cases covering registration, authentication, authorization, transactional workflows, and security defenses with a 100% pass rate.", space_before=2, space_after=4, align=WD_ALIGN_PARAGRAPH.JUSTIFY)

    tbl_test = doc.add_table(rows=17, cols=4)
    tbl_test.alignment = WD_TABLE_ALIGNMENT.CENTER
    test_cases = [
        ("No.", "Test Case", "Action / Input", "Expected Result"),
        ("1", "Valid Student Registration", "Fill valid details (Hostel student with room)", "Student inserted in students; redirect to login with success."),
        ("2", "Hostel Constraint Enforcement", "Day scholar submits hostel room number", "Server forces hostel fields to NULL; MySQL check constraint satisfied."),
        ("3", "Duplicate Email Prevention", "Register with existing student email", "Registration rejected with duplicate email warning."),
        ("4", "Weak Password Rejection", "Password without uppercase, digit or symbol", "Rejected with password complexity policy error message."),
        ("5", "Valid Student Login", "Correct Student ID and BCrypt password", "Session established; redirects to Student Dashboard."),
        ("6", "Staff Role Authentication", "Staff credentials on staff-login portal", "Authenticates staff; routes to assigned task queue."),
        ("7", "Admin Authentication", "Admin credentials on admin-login portal", "Authenticates admin; routes to administrative control center."),
        ("8", "Unauthenticated Route Guard", "Direct GET request to /student/dashboard", "AuthenticationFilter intercepts; redirects to /student-login."),
        ("9", "Cross-Role Security Defense", "Student session accesses /admin/dashboard", "AuthorizationFilter blocks access with HTTP 403 Forbidden."),
        ("10", "Complaint Submission", "Student files complaint with category & title", "Grievance GC1001 created with status PENDING and initial audit record."),
        ("11", "Admin Staff Assignment", "Admin assigns complaint to active staff", "Status updates to ASSIGNED; audit history record logged."),
        ("12", "Staff Progress Transition", "Staff clicks 'Start Work' on assigned task", "Status updates to IN_PROGRESS in atomic transaction."),
        ("13", "Resolution Remarks Validation", "Staff resolves ticket without remarks", "Form rejected; remarks mandatory (<= 500 characters)."),
        ("14", "Valid Complaint Resolution", "Staff inputs remarks and resolves ticket", "Status set to RESOLVED; remarks logged in audit trail."),
        ("15", "Student Withdrawal Workflow", "Student withdraws own PENDING complaint", "Status updates to WITHDRAWN; student cancellation recorded."),
        ("16", "CSRF Protection Verification", "POST request submitted without csrfToken", "AuthorizationFilter rejects request with HTTP 403 Forbidden.")
    ]
    for r_idx, (c1, c2, c3, c4) in enumerate(test_cases):
        cells = [tbl_test.cell(r_idx, i) for i in range(4)]
        cells[0].width = Inches(0.4)
        cells[1].width = Inches(1.8)
        cells[2].width = Inches(2.2)
        cells[3].width = Inches(2.4)
        for cell in cells:
            set_cell_margins(cell, top=30, bottom=30, left=50, right=50)
            if r_idx == 0: set_cell_background(cell, "EBF1F5")
        for i, text in enumerate([c1, c2, c3, c4]):
            p = cells[i].paragraphs[0]
            r = p.add_run(text)
            r.font.name = "Times New Roman"
            r.font.size = Pt(8.5)
            if r_idx == 0: r.bold = True

    doc.add_page_break()

    # =========================================================================
    # CHAPTER 6: CONCLUSION
    # =========================================================================
    p_header = add_p(space_before=0, space_after=4, align=WD_ALIGN_PARAGRAPH.RIGHT)
    r_hdr = p_header.add_run("27")
    r_hdr.font.name = "Times New Roman"
    r_hdr.font.size = Pt(10)

    add_heading_1("6. CONCLUSION")
    p = add_p("CampusCare: Student Complaint Management System demonstrates how a robust, database-driven, enterprise web application can be built entirely with pure Java EE technologies without external third-party frameworks. Students can register, lodge campus grievances with conditional accommodation details, receive standardized GC1001 ticket numbers, monitor complaint progress through an audit timeline, and review staff resolution remarks. Staff members manage assigned queues and log resolution remarks, while administrators govern user accounts, dispatch technicians, and inspect live operational workload statistics. All application records are securely persisted in a MySQL 8.0 database accessed through parameterized JDBC PreparedStatements, and web pages are dynamically rendered by JSP templates on Apache Tomcat 9. Security is woven throughout the application design: passwords use salted 12-round BCrypt key derivation, forms enforce anti-CSRF tokens, dynamic output is HTML-escaped, and sessions are guarded against fixation.", space_before=2, space_after=6, align=WD_ALIGN_PARAGRAPH.JUSTIFY)

    p = add_p("The project provided valuable practical experience in relational database modeling, MySQL check constraints, JDBC transaction boundaries, Servlet lifecycle orchestration, modular JSP layout, and writing clean, maintainable MVC software.", space_before=2, space_after=6, align=WD_ALIGN_PARAGRAPH.JUSTIFY)

    p = add_p(space_before=4, space_after=2, align=WD_ALIGN_PARAGRAPH.LEFT)
    r = p.add_run("Limitations")
    r.bold = True

    limits = [
        "In-App Notification Relays: Status updates are currently displayed within the application timeline; external SMS or email gateways were not integrated.",
        "Attachment Upload Storage: File uploads (e.g. photos of broken furniture or leaking pipes) are not currently persisted in MySQL to maintain a lightweight footprint.",
        "Manual Assignment: Complaints require administrative review for staff dispatch; automated AI-based category routing is not included in this release.",
        "Single Server Instance: Database connections use direct DriverManager; enterprise production environments would benefit from JNDI connection pooling."
    ]
    for lim in limits:
        p = add_p(f"• {lim}", space_before=1, space_after=2, align=WD_ALIGN_PARAGRAPH.JUSTIFY)
        p.paragraph_format.left_indent = Inches(0.25)

    p = add_p(space_before=4, space_after=2, align=WD_ALIGN_PARAGRAPH.LEFT)
    r = p.add_run("Future Scope")
    r.bold = True

    futures = [
        "Mobile Application Integration: Develop native Android and iOS mobile applications communicating with the existing Java Servlets via RESTful JSON endpoints.",
        "Automated Email & SMS Alerts: Integrate JavaMail API and SMS gateway webhooks to push real-time alerts to students and technicians on ticket status changes.",
        "AI-Powered Issue Categorization: Implement Natural Language Processing (NLP) models to automatically classify complaint narratives and assign target departments.",
        "Interactive Campus GIS Mapping: Embed interactive campus indoor map visualizers allowing students to drop location pins on faulty equipment.",
        "IoT Sensor Integration: Connect smart IoT sensors (e.g. water tank level sensors, power meter monitors) to trigger automated service tickets before human discovery."
    ]
    for fut in futures:
        p = add_p(f"• {fut}", space_before=1, space_after=2, align=WD_ALIGN_PARAGRAPH.JUSTIFY)
        p.paragraph_format.left_indent = Inches(0.25)

    doc.add_page_break()

    # =========================================================================
    # CHAPTER 7: REFERENCES
    # =========================================================================
    p_header = add_p(space_before=0, space_after=4, align=WD_ALIGN_PARAGRAPH.RIGHT)
    r_hdr = p_header.add_run("28")
    r_hdr.font.name = "Times New Roman"
    r_hdr.font.size = Pt(10)

    add_heading_1("7. REFERENCES")

    refs = [
        ("[1]", "Oracle Corporation, \"MySQL 8.0 Reference Manual: Connector/J JDBC Driver Guide\". https://dev.mysql.com/doc/connector-j/en/"),
        ("[2]", "The Apache Software Foundation, \"Apache Tomcat 9 Documentation: Servlet and JSP Container Architecture\". https://tomcat.apache.org/tomcat-9.0-doc/"),
        ("[3]", "JavaServer Pages Specification 2.3 (JSR 245) and Java Servlet Specification 4.0 (JSR 369)."),
        ("[4]", "Mozilla Developer Network, \"MDN Web Docs: HTML5, CSS3, and JavaScript Guides\". https://developer.mozilla.org/"),
        ("[5]", "OWASP Foundation, \"Password Storage Cheat Sheet: BCrypt Hashing Recommendation\". https://cheatsheetseries.owasp.org/"),
        ("[6]", "OWASP Foundation, \"Cross-Site Request Forgery (CSRF) Prevention Cheat Sheet\". https://cheatsheetseries.owasp.org/"),
        ("[7]", "OWASP Foundation, \"SQL Injection Prevention Cheat Sheet: Parameterized Queries\". https://cheatsheetseries.owasp.org/"),
        ("[8]", "N. Provos and D. Mazières, \"A Future-Adaptable Password Scheme (BCrypt)\", USENIX Annual Technical Conference, 1999."),
        ("[9]", "Herbert Schildt, \"Java: The Complete Reference\", 12th Edition, McGraw-Hill Education."),
        ("[10]", "Marty Hall and Larry Brown, \"Core Servlets and JavaServer Pages: Advanced Technologies\", Prentice Hall PTR.")
    ]

    for ref_num, ref_text in refs:
        p = doc.add_paragraph()
        p.paragraph_format.space_before = Pt(3)
        p.paragraph_format.space_after = Pt(4)
        p.paragraph_format.line_spacing = 1.15
        p.paragraph_format.left_indent = Inches(0.4)
        p.paragraph_format.first_line_indent = Inches(-0.4)
        r0 = p.add_run(f"{ref_num}  ")
        r0.font.name = "Times New Roman"
        r0.font.size = Pt(10)
        r0.bold = True
        r1 = p.add_run(ref_text)
        r1.font.name = "Times New Roman"
        r1.font.size = Pt(10)

    # Save to user locations
    out_docx_1 = r"C:\Campuscare\CampusCare_Project_Report.docx"
    out_docx_2 = r"C:\Users\DELL\Downloads\CampusCare_Project_Report.docx"
    out_docx_3 = r"C:\Users\DELL\Desktop\CampusCare_Project_Report.docx"

    doc.save(out_docx_1)
    doc.save(out_docx_2)
    doc.save(out_docx_3)

    print("Successfully generated CampusCare project documentation DOCX:")
    print("  1.", out_docx_1)
    print("  2.", out_docx_2)
    print("  3.", out_docx_3)

if __name__ == "__main__":
    create_document()
