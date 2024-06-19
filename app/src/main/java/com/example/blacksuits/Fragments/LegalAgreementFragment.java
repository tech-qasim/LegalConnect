package com.example.blacksuits.Fragments;


import static java.util.Locale.filter;

import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.blacksuits.DataClass.LegalAgreementDataClass;
import com.example.blacksuits.DataClass.ProfilePictureDataClass;
import com.example.blacksuits.R;
import com.example.blacksuits.Adapters.RecyclerViewAdapterForUserAndLawyerScreen;
import com.example.blacksuits.Adapters.RecyclerViewAdapterForLegalAgreement;
import com.example.blacksuits.SharedPreferences.MySharedPreferences;

import java.util.ArrayList;

public class LegalAgreementFragment extends Fragment {
    RecyclerViewAdapterForLegalAgreement adapter;

    private LinearLayout editTextContainer;

    ArrayList<LegalAgreementDataClass> data = new ArrayList<>();

    private MySharedPreferences mySharedPreferences;
    public LegalAgreementFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_legal_agreement, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        data.clear();

        mySharedPreferences = new MySharedPreferences(requireContext());


        ImageView profilePicture = view.findViewById(R.id.profile_picture_legal_agreement);
        profilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new EditProfile());
            }
        });

        ProfilePictureDataClass.getCurrentProfilePicStorageRef(mySharedPreferences.getUserID()).getDownloadUrl()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful())
                    {
                        Uri uri = task.getResult();
                        ProfilePictureDataClass.setProfilePic(getContext(),uri,profilePicture);
                    }

                });


        if(mySharedPreferences.getImageUri()!=null) {
            ProfilePictureDataClass.setProfilePic(getContext(), mySharedPreferences.getImageUri(), profilePicture);
        }


        searchingMechanism(view);
        addContent();
        buildRecyclerView(view);


    }


    private void searchingMechanism(View view)
    {
        ImageView searchButton = view.findViewById(R.id.searchButton);
        searchButton.setOnClickListener(new View.OnClickListener() {

            boolean editTextAdded = false;
            @Override
            public void onClick(View view) {

                if (!editTextAdded) {
                    editTextContainer = getView().findViewById(R.id.editTextContainer);
                    CardView backButton = getView().findViewById(R.id.back_button);
                    TextView toolbarTitle = getView().findViewById(R.id.toolbarTitle);

                    // Create a new EditText
                    EditText editText = new EditText(view.getContext());

                    // Set any properties for the EditText if needed
                    editText.setHint("Enter text...");

                    editText.setMaxLines(1);

                    // Set input type to text
                    editText.setInputType(InputType.TYPE_CLASS_TEXT);

                    editText.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);

                    // Add the EditText to the LinearLayout
                    editTextContainer.addView(editText);

                    editText.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                        }

                        @Override
                       public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                            adapter.filter(charSequence.toString());
                        }

                        @Override
                        public void afterTextChanged(Editable editable) {
////                            String query = editable.toString();
////                            adapter.filter(query);

                        }
                    });

                    // Set the visibility of the LinearLayout to VISIBLE
                    editTextContainer.setVisibility(View.VISIBLE);
                    backButton.setVisibility(View.GONE);
                    toolbarTitle.setVisibility(View.GONE);
                    editTextAdded = true;


                }
            }
        });
    }


    private void addContent ()
    {
        data.add(new LegalAgreementDataClass(
                "Criminal Cases",
                "These involve the prosecution of individuals or entities accused of committing a crime against the state or society. The burden of proof lies with the prosecution, and if found guilty, the defendant may face penalties such as imprisonment, fines, or probation."
        ));

        data.add(new LegalAgreementDataClass(
                "Violent Crimes",
                "Violent crimes involve the use or threat of force against another person. Examples include murder, manslaughter, assault, robbery, and domestic violence. These offenses are often prosecuted aggressively due to their serious nature and potential impact on public safety."
        ));

        data.add(new LegalAgreementDataClass(
                "Property Crimes",
                "Property crimes involve the theft or destruction of someone else's property. Examples include burglary (breaking into a building with the intent to commit a crime), theft (taking someone else's property without permission), arson (intentionally setting fire to property), and vandalism (willful destruction of property)."
        ));

        data.add(new LegalAgreementDataClass(
                "Drug Crimes",
                "Drug crimes involve the possession, distribution, or manufacturing of illegal substances such as cocaine, heroin, methamphetamine, and marijuana (in jurisdictions where it is illegal). These offenses are prosecuted under state and federal drug laws and can carry significant penalties, including fines and imprisonment."
        ));

        data.add(new LegalAgreementDataClass(
                "White-Collar Crimes",
                "White-collar crimes are non-violent offenses typically committed by individuals or businesses for financial gain. Examples include fraud, embezzlement, money laundering, insider trading, and identity theft. These cases often involve complex financial transactions and may be prosecuted by specialized units within law enforcement agencies."
        ));

        data.add(new LegalAgreementDataClass(
                "Sex Crimes",
                "Sex crimes involve illegal or non-consensual sexual conduct. Examples include rape, sexual assault, sexual harassment, child pornography, and indecent exposure. Sex crime cases can be highly sensitive and often require specialized investigation and prosecution techniques."
        ));

        data.add(new LegalAgreementDataClass(
                "Civil Cases",
                "Civil cases deal with disputes between individuals or entities, typically involving matters such as contracts, property rights, personal injury, or family law issues like divorce or child custody. The burden of proof is usually on the plaintiff, and remedies may include monetary damages or equitable relief."
        ));

        data.add(new LegalAgreementDataClass(
                "Personal Injury Cases",
                "Personal injury cases involve claims for physical or emotional harm suffered by an individual due to the negligence or intentional actions of another party. This includes incidents such as car accidents, slip and fall accidents, medical malpractice, product liability, and injuries sustained on someone else's property."
        ));

        data.add(new LegalAgreementDataClass(
                "Contract Disputes",
                "Contract disputes arise when parties to a contract disagree about its terms, performance, or enforcement. This could involve issues such as breach of contract, failure to deliver goods or services, disputes over payment, or disagreements about the interpretation of contract provisions."
        ));

        data.add(new LegalAgreementDataClass(
                "Property Disputes",
                "Property disputes involve disagreements over the ownership, use, or rights associated with real property (land and buildings) or personal property (e.g., vehicles, jewelry). Examples include boundary disputes, landlord-tenant conflicts, easement disputes, or disagreements over property titles."
        ));

        data.add(new LegalAgreementDataClass(
                "Family Law Matters",
                "Family law cases encompass legal issues related to familial relationships, such as divorce, child custody, child support, spousal support (alimony), adoption, paternity disputes, and domestic violence restraining orders."
        ));

        data.add(new LegalAgreementDataClass(
                "Employment Disputes",
                "Employment disputes involve conflicts between employers and employees. This includes claims of wrongful termination, discrimination (based on factors like race, gender, age, or disability), harassment, retaliation, wage and hour violations, or breaches of employment contracts."
        ));

        data.add(new LegalAgreementDataClass(
                "Tort Claims",
                "Tort claims are civil actions seeking compensation for harm or injury caused by the wrongful actions of another party. This encompasses various types of wrongdoing, including negligence (e.g., car accidents, slip and falls), intentional torts (e.g., assault, defamation), and strict liability (e.g., product defects)."
        ));

        data.add(new LegalAgreementDataClass(
                "Debt Collection",
                "Debt collection cases arise when a creditor seeks to recover money owed by a debtor. This could involve unpaid loans, credit card debt, medical bills, or outstanding invoices. Debt collection proceedings can involve negotiations, lawsuits, and court judgments to enforce repayment."
        ));

        data.add(new LegalAgreementDataClass(
                "Probate and Estate Matters",
                "Probate and estate cases involve the administration of a deceased person's estate, including distributing assets to beneficiaries, paying off debts and taxes, and resolving disputes over wills or trusts."
        ));

        data.add(new LegalAgreementDataClass(
                "Consumer Protection Cases",
                "Consumer protection cases involve disputes between consumers and businesses regarding unfair or deceptive practices, breach of warranties, product defects, fraud, or violations of consumer rights laws."
        ));

        data.add(new LegalAgreementDataClass(
                "Land Use and Zoning Disputes",
                "These cases involve conflicts over land development, building permits, zoning regulations, or other land use restrictions imposed by local governments or property owners' associations."
        ));

        data.add(new LegalAgreementDataClass(
                "Administrative Cases",
                "These involve disputes related to administrative agencies and their decisions or regulations."
        ));

        data.add(new LegalAgreementDataClass(
                "Regulatory Compliance",
                "These cases involve challenges to regulations or rules promulgated by administrative agencies. Parties may argue that the regulations are unconstitutional, exceed the agency's authority, or are arbitrary and capricious."
        ));

        data.add(new LegalAgreementDataClass(
                "License Revocation or Suspension",
                "Administrative agencies often have the authority to issue licenses or permits for various activities (e.g., professional licenses, business permits). Cases may arise when the agency seeks to revoke or suspend a license based on alleged violations of regulations or standards."
        ));

        data.add(new LegalAgreementDataClass(
                "Administrative Appeals",
                "Individuals or businesses may file administrative appeals to challenge decisions made by agencies. This could include appealing a denial of a permit, license, or government benefit, or contesting a penalty or enforcement action imposed by the agency."
        ));

        data.add(new LegalAgreementDataClass(
                "Environmental Law Cases",
                "Administrative agencies such as the Environmental Protection Agency (EPA) regulate activities that impact the environment, including air and water quality, waste management, and land use. Cases may involve challenges to agency decisions related to environmental permits, compliance with environmental laws, or enforcement actions against polluters."
        ));

        data.add(new LegalAgreementDataClass(
                "Labor and Employment Disputes",
                "Administrative agencies such as the Equal Employment Opportunity Commission (EEOC) and the Department of Labor oversee employment-related matters, including discrimination complaints, wage and hour disputes, workplace safety violations, and unemployment insurance claims."
        ));

        data.add(new LegalAgreementDataClass(
                "Tax Appeals",
                "Taxpayers may challenge decisions made by tax agencies regarding tax assessments, audits, refunds, or penalties. Cases may be heard by administrative tax tribunals or boards before proceeding to court."
        ));

        data.add(new LegalAgreementDataClass(
                "Social Security and Disability Claims",
                "Administrative agencies such as the Social Security Administration (SSA) handle claims for disability benefits, retirement benefits, and supplemental security income. Cases may involve appeals of benefit denials, terminations, or reductions."
        ));

        data.add(new LegalAgreementDataClass(
                "Immigration Cases",
                "Immigration-related administrative cases involve challenges to decisions made by immigration agencies, such as the U.S. Citizenship and Immigration Services (USCIS) or the Board of Immigration Appeals (BIA). This could include appeals of visa denials, deportation orders, or asylum claims."
        ));

        data.add(new LegalAgreementDataClass(
                "Healthcare Regulation and Licensing",
                "Administrative agencies oversee healthcare regulation and licensing for medical professionals, healthcare facilities, pharmaceuticals, and health insurance providers. Cases may involve disciplinary actions against healthcare practitioners, disputes over regulatory compliance, or challenges to licensing decisions."
        ));

        data.add(new LegalAgreementDataClass(
                "Family Law Cases",
                "Family law cases encompass legal matters concerning familial relationships, including divorce, child custody, adoption, paternity disputes, and domestic violence protection orders."
        ));

        data.add(new LegalAgreementDataClass(
                "Divorce",
                "Divorce cases involve the legal termination of a marriage. This includes issues such as property division, spousal support (alimony), child custody, visitation rights, and child support. Divorce cases can be contested (where parties disagree on terms) or uncontested (where parties agree on terms)."
        ));

        data.add(new LegalAgreementDataClass(
                "Child Custody and Visitation",
                "These cases determine the legal and physical custody of children when parents separate or divorce. Legal custody refers to decision-making authority regarding the child's upbringing, while physical custody refers to where the child resides. Visitation arrangements, also known as parenting time, may also be established for non-custodial parents."
        ));

        data.add(new LegalAgreementDataClass(
                "Child Support",
                "Child support cases involve determining the financial support that one parent must provide to the other parent for the care and upbringing of their child. Child support calculations consider factors such as the income of both parents, the child's needs, and the custody arrangement."
        ));

        data.add(new LegalAgreementDataClass(
                "Spousal Support (Alimony)",
                "Spousal support cases involve determining whether one spouse should pay financial support to the other spouse following separation or divorce. Factors considered in spousal support determinations include the length of the marriage, the financial needs of each spouse, and their respective earning capacities."
        ));

        data.add(new LegalAgreementDataClass(
                "Adoption",
                "Adoption cases involve the legal process of establishing a parent-child relationship between individuals who are not biologically related. This can include stepparent adoptions, agency adoptions, international adoptions, and adoptions of foster children. Adoption proceedings require court approval and may involve termination of parental rights for biological parents."
        ));

        data.add(new LegalAgreementDataClass(
                "Paternity Disputes",
                "Paternity cases involve establishing or disputing the legal father-child relationship. This may be necessary for purposes of child support, custody, visitation, inheritance rights, and benefits such as health insurance or social security. Paternity can be established voluntarily through acknowledgment or involuntarily through genetic testing."
        ));

        data.add(new LegalAgreementDataClass(
                "Domestic Violence Protection Orders",
                "Domestic violence cases involve seeking protection orders (restraining orders) against individuals who have committed acts of domestic violence or abuse. Protection orders may include provisions for restraining the abuser from contacting or approaching the victim and may also address custody and visitation arrangements."
        ));

        data.add(new LegalAgreementDataClass(
                "Modification and Enforcement Proceedings",
                "Family law cases may involve modifications to existing court orders (e.g., custody, child support, alimony) due to changes in circumstances such as job loss, relocation, or remarriage. Enforcement proceedings may be initiated when one party fails to comply with court orders, such as failing to pay child support or violating visitation rights."
        ));

        data.add(new LegalAgreementDataClass(
                "Guardianship and Conservatorship",
                "Guardianship and conservatorship cases involve the appointment of a legal guardian or conservator to make decisions on behalf of an incapacitated adult or minor. This may be necessary when an individual is unable to manage their own affairs due to age, illness, disability, or other circumstances."
        ));

        data.add(new LegalAgreementDataClass(
                "Contract Cases",
                "These cases involve disputes arising from agreements between parties, including breach of contract claims, disputes over the interpretation of contract terms, or claims for specific performance of contractual obligations."
        ));

        data.add(new LegalAgreementDataClass(
                "Breach of Contract",
                "Breach of contract cases occur when one party fails to fulfill its obligations under a contract. This could involve failing to deliver goods or services as promised, failing to make payments on time, or otherwise violating the terms of the agreement. The non-breaching party may seek damages for losses incurred as a result of the breach."
        ));

        data.add(new LegalAgreementDataClass(
                "Contract Interpretation",
                "Contract interpretation cases arise when the parties disagree on the meaning or scope of contract terms. This could include disputes over ambiguous language, conflicting provisions, or the intent of the parties at the time the contract was formed. Courts may interpret contracts based on the plain language of the agreement, the parties' intentions, or industry customs and practices."
        ));

        data.add(new LegalAgreementDataClass(
                "Impossibility or Impracticability",
                "These cases involve situations where performance of a contract becomes impossible or impracticable due to unforeseen circumstances such as natural disasters, government regulations, or the death or incapacity of a party. Depending on the circumstances, the affected party may be excused from performing its obligations under the contract."
        ));

        data.add(new LegalAgreementDataClass(
                "Fraudulent Misrepresentation",
                "Fraudulent misrepresentation cases occur when one party knowingly makes false statements or conceals material facts during contract negotiations, inducing the other party to enter into the contract. The defrauded party may seek to rescind the contract, recover damages, or pursue other remedies available under the law."
        ));

        data.add(new LegalAgreementDataClass(
                "Property Cases",
                "Property cases deal with disputes over real property (land and buildings) or personal property (such as vehicles, jewelry, or intellectual property). Examples include boundary disputes, landlord-tenant conflicts, and eminent domain proceedings."
        ));

        data.add(new LegalAgreementDataClass(
                "Real Property Disputes",
                "Boundary Disputes: Boundary disputes arise when adjacent property owners disagree about the location of property lines or boundaries. These disputes may involve conflicting surveys, encroachments, or adverse possession claims.\nEasement Disputes: Easement disputes involve disagreements over the right to use or access another person's property for a specific purpose, such as a right-of-way for utilities, access to a water source, or passage across a driveway.\nLandlord-Tenant Disputes: Landlord-tenant disputes involve disagreements between landlords and tenants regarding lease agreements, rent payments, repairs and maintenance, eviction proceedings, security deposits, and habitability issues.\nZoning and Land Use Disputes: Zoning and land use disputes arise when property owners challenge zoning regulations, land use restrictions, or government decisions affecting property development, such as rezoning applications, building permits, or variances.\nQuiet Title Actions: Quiet title actions are legal proceedings to resolve disputes over the ownership or title to real property. These cases may be initiated to clear title defects, resolve competing claims to property, or establish ownership rights."
        ));

        data.add(new LegalAgreementDataClass(
                "Personal Property Disputes",
                "Ownership Disputes: Ownership disputes involve conflicts over the ownership or possession of personal property. This could include disputes over stolen property, lost property, abandoned property, or property obtained through fraud or deception.\nConversion and Theft: Conversion and theft cases involve allegations that someone wrongfully took, used, or damaged another person's personal property without permission. The aggrieved party may seek damages for the value of the property or return of the property itself.\nBailment Disputes: Bailment disputes arise when personal property is entrusted to another party for safekeeping, storage, or transportation, and the property is lost, damaged, or mishandled. The parties may dispute liability for the loss or damage and seek compensation for any losses incurred.\nIntellectual Property Disputes: Intellectual property disputes involve conflicts over the ownership, use, or infringement of intellectual property rights, including patents, trademarks, copyrights, and trade secrets. These cases may involve claims of infringement, misappropriation, or unauthorized use of intellectual property."
        ));

        data.add(new LegalAgreementDataClass(
                "Foreclosure and Lien Actions",
                "Foreclosure Proceedings: Foreclosure proceedings occur when a lender seeks to enforce its rights under a mortgage or deed of trust by seizing and selling the property to satisfy the debt owed by the borrower (mortgagor).\nLien Actions: Lien actions involve disputes over the validity, priority, or enforcement of liens placed on real or personal property to secure payment of a debt. These may include mechanics' liens, judgment liens, tax liens, or other types of liens."
        ));

        data.add(new LegalAgreementDataClass(
                "Adverse Possession Claims",
                "Adverse possession claims involve legal actions to establish ownership of real property based on open, notorious, continuous, and hostile possession of the property for a statutory period (often several years), in a manner inconsistent with the true owner's rights."
        ));

        data.add(new LegalAgreementDataClass(
                "Employment Cases",
                "These cases involve disputes between employers and employees, including claims of discrimination, wrongful termination, wage and hour violations, workplace harassment, or breach of employment contracts."
        ));

        data.add(new LegalAgreementDataClass(
                "Discrimination Claims",
                "Discrimination cases involve allegations that an employer treated an employee unfairly or unfavorably based on a protected characteristic such as race, color, sex, religion, national origin, age, disability, or genetic information. These cases may arise in the context of hiring, firing, promotion, compensation, or other terms and conditions of employment."
        ));

        data.add(new LegalAgreementDataClass(
                "Wrongful Termination",
                "Wrongful termination cases involve allegations that an employer unlawfully terminated an employee's employment in violation of federal or state law, employment contracts, or public policy. This could include termination based on discrimination, retaliation for whistleblowing, exercising legal rights, or taking protected leave under the Family and Medical Leave Act (FMLA)."
        ));

        data.add(new LegalAgreementDataClass(
                "Retaliation Claims",
                "Retaliation cases involve allegations that an employer took adverse action against an employee (such as termination, demotion, or harassment) in retaliation for the employee engaging in protected activity, such as reporting discrimination, harassment, safety violations, or illegal conduct by the employer."
        ));

        data.add(new LegalAgreementDataClass(
                "Wage and Hour Violations",
                "Wage and hour cases involve allegations that an employer failed to comply with federal or state laws governing minimum wage, overtime pay, meal and rest breaks, recordkeeping, or classification of employees as exempt or non-exempt from overtime pay requirements. Common violations include unpaid wages, unpaid overtime, misclassification of employees as independent contractors, and off-the-clock work."
        ));

        data.add(new LegalAgreementDataClass(
                "Sexual Harassment Claims",
                "Sexual harassment cases involve allegations of unwelcome sexual advances, requests for sexual favors, or other verbal and physical conduct of a sexual nature that creates a hostile work environment or results in adverse employment actions. Employers may be held liable for sexual harassment by supervisors, co-workers, or third parties if they fail to take prompt and appropriate corrective action."
        ));

        data.add(new LegalAgreementDataClass(
                "Whistleblower Claims",
                "Whistleblower cases involve allegations that an employer retaliated against an employee for reporting illegal or unethical conduct within the organization, such as fraud, safety violations, environmental violations, or violations of securities laws. Whistleblower protections may be provided under federal or state laws, as well as through internal corporate policies."
        ));

        data.add(new LegalAgreementDataClass(
                "Employment Contract Disputes",
                "Employment contract disputes involve disagreements over the terms, interpretation, or enforcement of employment contracts, including issues such as compensation, benefits, job duties, non-compete agreements, non-disclosure agreements, or severance agreements."
        ));

        data.add(new LegalAgreementDataClass(
                "Family and Medical Leave Act (FMLA) Violations",
                "FMLA cases involve allegations that an employer interfered with an employee's rights under the FMLA to take unpaid leave for qualified medical or family reasons, or retaliated against an employee for exercising FMLA rights. Common violations include denial of leave, failure to restore the employee to the same or equivalent position upon return from leave, or retaliation for taking FMLA leave."
        ));

        data.add(new LegalAgreementDataClass(
                "Americans with Disabilities Act (ADA) Violations",
                "ADA cases involve allegations that an employer discriminated against an employee or applicant with a disability by failing to provide reasonable accommodations, subjecting the individual to harassment or disparate treatment, or taking adverse employment actions based on disability."
        ));

        data.add(new LegalAgreementDataClass(
                "Tort Cases",
                "Tort cases involve civil wrongs that result in harm or injury to individuals or property. Common types of torts include negligence, intentional torts, strict liability, nuisance, defamation, fraud, and trespass."
        ));

        data.add(new LegalAgreementDataClass(
                "Negligence",
                "Negligence cases arise when someone fails to exercise reasonable care, resulting in harm to another person or property. To prove negligence, the plaintiff must show that the defendant owed a duty of care, breached that duty, and caused the plaintiff's injuries or damages. Examples include car accidents, slip and fall accidents, medical malpractice, and premises liability cases."
        ));

        data.add(new LegalAgreementDataClass(
                "Intentional Torts",
                "Intentional torts involve deliberate or purposeful actions that result in harm to another person or property. Unlike negligence, intentional torts require proof of intent to cause harm or knowledge that harm was likely to occur. Examples include assault, battery, false imprisonment, intentional infliction of emotional distress, defamation (libel or slander), trespass to land or chattels, and conversion (wrongful deprivation of property)."
        ));

        data.add(new LegalAgreementDataClass(
                "Strict Liability",
                "Strict liability cases hold parties liable for harm caused by certain activities or products regardless of fault or intent. Under strict liability, the plaintiff does not need to prove negligence or intent, only that the harm occurred as a result of the defendant's actions. Common examples include product liability cases (defective products causing injury) and ultrahazardous activities (such as storing explosives or keeping wild animals)."
        ));

        data.add(new LegalAgreementDataClass(
                "Nuisance",
                "Nuisance cases involve interference with a person's use or enjoyment of their property. Nuisances may be classified as either public (affecting a large number of people) or private (affecting an individual or small group). Examples include noise pollution, odors, pollution of water or air, and obstructions that interfere with the use of property."
        ));

        data.add(new LegalAgreementDataClass(
                "Defamation",
                "Defamation cases involve false statements that harm a person's reputation or character. Defamation can be categorized as libel (written or recorded statements) or slander (spoken statements). To prevail in a defamation case, the plaintiff must prove that the defendant made a false statement of fact, published it to a third party, and caused harm to the plaintiff's reputation."
        ));

        data.add(new LegalAgreementDataClass(
                "Fraud",
                "Fraud cases involve intentional misrepresentation or deceit that causes harm to another person. To establish fraud, the plaintiff must show that the defendant made a false statement of fact, with knowledge of its falsity or reckless disregard for the truth, with the intent to deceive the plaintiff, and that the plaintiff relied on the false statement to their detriment."
        ));

        data.add(new LegalAgreementDataClass(
                "Trespass",
                "Trespass cases involve unauthorized entry onto another person's property. Trespass can occur by physical intrusion onto land (trespass to real property) or by interference with personal property (trespass to chattels). Even minimal intrusion without causing damage may constitute trespass."
        ));

    }


    private void buildRecyclerView (View view)
    {
        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewLegalAgreement);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new RecyclerViewAdapterForLegalAgreement(this.getLayoutInflater(), data);
        adapter.setClickListener((RecyclerViewAdapterForUserAndLawyerScreen.ItemClickListener) requireContext());
        recyclerView.setAdapter(adapter);
    }

    private void loadFragment (Fragment fragment)
    {
        FragmentManager fragmentManager = getParentFragmentManager();

        // Start a FragmentTransaction
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        // Replace the existing fragment with the new one
        fragmentTransaction.replace(R.id.fragmentContainer, fragment);

        // Add the transaction to the back stack
        fragmentTransaction.addToBackStack(null);

        // Commit the transaction
        fragmentTransaction.commit();
    }




}