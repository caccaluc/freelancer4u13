package ch.zhaw.freelancer4u.controller;

import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ch.zhaw.freelancer4u.model.Job;
import ch.zhaw.freelancer4u.model.JobCreateDTO;
import ch.zhaw.freelancer4u.model.JobType;
import ch.zhaw.freelancer4u.repository.JobRepository;
import ch.zhaw.freelancer4u.service.CompanyService;
import ch.zhaw.freelancer4u.service.RoleService;

@RestController
@RequestMapping("/api")
public class JobController {

    @Autowired
    JobRepository jobRepository;

    @Autowired
    CompanyService companyService;

    @Autowired
    RoleService roleService;
    
    @Autowired
    OpenAiChatModel chatModel;

    @PostMapping("/job")
    public ResponseEntity<Job> createJob(@RequestBody JobCreateDTO cDTO) {
        if (!roleService.userHasRole("admin")) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        if (!companyService.companyExists(cDTO.getCompanyId())) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        var generatedTitle = chatModel.call(new Prompt("Erstelle einen kurzen Titel für den Job mit der Beschreibung:  " + cDTO.getDescription()));
        var title = generatedTitle.getResult().getOutput().getContent();
        Job jDAO = new Job(title, cDTO.getDescription(), cDTO.getJobType(), cDTO.getEarnings(), cDTO.getCompanyId());
        Job j = jobRepository.save(jDAO);
        return new ResponseEntity<>(j, HttpStatus.CREATED);
    }

    @GetMapping("/job")
    public ResponseEntity<Page<Job>> getAllJobs(
            @RequestParam(required = false) Double min,
            @RequestParam(required = false) JobType type,
            @RequestParam(required = false, defaultValue = "1") Integer pageNumber,
            @RequestParam(required = false, defaultValue = "5") Integer pageSize) {
        Page<Job> allJobs;
        if (min == null && type == null) {
            allJobs = jobRepository.findAll(PageRequest.of(pageNumber - 1, pageSize));
        } else {
            if (min != null && type != null) {
                allJobs = jobRepository.findByJobTypeAndEarningsGreaterThan(type, min,
                        PageRequest.of(pageNumber - 1, pageSize));
            } else if (min != null) {
                allJobs = jobRepository.findByEarningsGreaterThan(min, PageRequest.of(pageNumber - 1, pageSize));
            } else {
                allJobs = jobRepository.findByJobType(type, PageRequest.of(pageNumber - 1, pageSize));
            }
        }        
        return new ResponseEntity<>(allJobs, HttpStatus.OK);
    }

    @GetMapping("/job/{id}")
    public ResponseEntity<Job> getJobById(@PathVariable String id) {
        var job = jobRepository.findById(id);
        if (job.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(job.get(), HttpStatus.OK);
    }

    @DeleteMapping("/job")
    public ResponseEntity<String> deleteAllJobs() {
        if (!roleService.userHasRole("admin")) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        jobRepository.deleteAll();
        return ResponseEntity.status(HttpStatus.OK).body("DELETED");
    }

    @DeleteMapping("/job/{id}")
    public ResponseEntity<String> deleteJobById(@PathVariable String id) {
        if (!roleService.userHasRole("admin")) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        jobRepository.deleteById(id);
        return ResponseEntity.status(HttpStatus.OK).body("DELETED");
    }
}
