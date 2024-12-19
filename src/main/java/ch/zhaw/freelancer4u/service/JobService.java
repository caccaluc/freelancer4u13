package ch.zhaw.freelancer4u.service;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ch.zhaw.freelancer4u.model.Job;
import ch.zhaw.freelancer4u.model.JobState;
import ch.zhaw.freelancer4u.repository.JobRepository;

@Service
public class JobService {
    @Autowired
    JobRepository jobRepository;
  
    public Optional<Job> assignJob(String jobId, String freelancerId) {
        Optional<Job> jobOptional = jobRepository.findById(jobId);
        if (jobOptional.isPresent()) {
            Job job = jobOptional.get();
            if (job.getJobState() == JobState.NEW) {
                job.setFreelancerId(freelancerId);
                job.setJobState(JobState.ASSIGNED);
                jobRepository.save(job);
                return Optional.of(job);
            }
        }
        return Optional.empty();    
    }

    public Optional<Job> completeJob(String jobId, String freelancerId) {
        Optional<Job> jobOptional = jobRepository.findById(jobId);
        if (jobOptional.isPresent()) {
            Job job = jobOptional.get();
            if (job.getJobState() == JobState.ASSIGNED && job.getFreelancerId().equals(freelancerId)) {
                job.setJobState(JobState.DONE);
                jobRepository.save(job);
                return Optional.of(job);
            }
        }
        return Optional.empty();    
    }
}
