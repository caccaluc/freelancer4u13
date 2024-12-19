package ch.zhaw.freelancer4u.controller;

import java.util.Optional;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ch.zhaw.freelancer4u.model.Job;
import ch.zhaw.freelancer4u.model.JobState;
import ch.zhaw.freelancer4u.model.JobStateAggregation;
import ch.zhaw.freelancer4u.model.JobStateChangeDTO;
import ch.zhaw.freelancer4u.model.Mail;
import ch.zhaw.freelancer4u.service.CompanyService;
import ch.zhaw.freelancer4u.service.JobService;
import ch.zhaw.freelancer4u.service.MailService;
import ch.zhaw.freelancer4u.service.RoleService;
import ch.zhaw.freelancer4u.service.UserService;
import ch.zhaw.freelancer4u.repository.JobRepository;

@RestController
@RequestMapping("/api/service")
public class ServiceController {

    @Autowired
    JobService jobService;

    @Autowired
    JobRepository jobRepository;

    @Autowired
    RoleService roleService;

    @Autowired
    UserService userService;

    @Autowired
    CompanyService companyService;

    @Autowired
    MailService mailService;

    @PutMapping("/assignjob")
    public ResponseEntity<Job> assignJob(@RequestBody JobStateChangeDTO changeS) {
        if (!roleService.userHasRole("admin")) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        String freelancerId = changeS.getFreelancerId();
        String jobId = changeS.getJobId();

        Optional<Job> job = jobService.assignJob(jobId, freelancerId);
        if (job.isPresent()) {
            return new ResponseEntity<>(job.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @PutMapping("/completejob")
    public ResponseEntity<Job> completeJob(@RequestBody JobStateChangeDTO changeS) {
        if (!roleService.userHasRole("admin")) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        String freelancerId = changeS.getFreelancerId();
        String jobId = changeS.getJobId();
        Optional<Job> job = jobService.completeJob(jobId, freelancerId);
        if (job.isPresent()) {
            return new ResponseEntity<>(job.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/jobdashboard")
    public List<JobStateAggregation> getJobStateAggregation(@RequestParam String company) {
        return jobRepository.getJobStateAggregation(company);
    }

    @PutMapping("/me/assignjob")
    public ResponseEntity<Job> assignToMe(@RequestParam String jobId) {
        String userEmail = userService.getEmail();
        Optional<Job> job = jobService.assignJob(jobId, userEmail);
        if (job.isPresent()) {
            sendMail(userEmail, job);
            return new ResponseEntity<>(job.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @PutMapping("/me/completejob")
    public ResponseEntity<Job> completeMyJob(@RequestParam String jobId) {
        String userEmail = userService.getEmail();
        Optional<Job> job = jobService.completeJob(jobId, userEmail);
        if (job.isPresent()) {
            sendMail(userEmail, job);
            return new ResponseEntity<>(job.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    private void sendMail(String freelancerEmail, Optional<Job> job) {
        var mail = new Mail();
        mail.setTo(freelancerEmail);
        mail.setSubject("Assigned job " + job.get().getDescription() + " with status " + job.get().getJobState());

        String mailMessage = "Hi, the job " + job.get().getDescription() + " was assigned to you. The new status is " + job.get().getJobState();
        if(job.isPresent() && job.isPresent() && job.get().getJobState().equals(JobState.DONE)){
            mailMessage = "Hi, the job " + job.get().getDescription() + " was marked as " + job.get().getJobState();
        }
        mail.setMessage(mailMessage);
        mailService.sendMail(mail);
    }

}
